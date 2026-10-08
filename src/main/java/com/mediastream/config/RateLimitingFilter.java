package com.mediastream.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RateLimitingFilter extends OncePerRequestFilter {

    private static final int MAX_REQUESTS_PER_MINUTE_GENERAL = 120;
    private static final int MAX_REQUESTS_PER_MINUTE_AUTH = 30;

    private static class RequestCounter {
        final AtomicInteger count = new AtomicInteger(0);
        volatile long windowStartMs = System.currentTimeMillis();

        boolean allowRequest(int maxAllowed) {
            long now = System.currentTimeMillis();
            if (now - windowStartMs > 60_000) {
                synchronized (this) {
                    if (now - windowStartMs > 60_000) {
                        windowStartMs = now;
                        count.set(0);
                    }
                }
            }
            return count.incrementAndGet() <= maxAllowed;
        }

        int getRemaining(int maxAllowed) {
            return Math.max(0, maxAllowed - count.get());
        }
    }

    private final ConcurrentHashMap<String, RequestCounter> requestCounts = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String clientIp = getClientIp(request);
        String path = request.getRequestURI();

        boolean isAuthEndpoint = path.startsWith("/api/auth/");
        int limit = isAuthEndpoint ? MAX_REQUESTS_PER_MINUTE_AUTH : MAX_REQUESTS_PER_MINUTE_GENERAL;

        String key = (isAuthEndpoint ? "AUTH:" : "GEN:") + clientIp;
        RequestCounter counter = requestCounts.computeIfAbsent(key, k -> new RequestCounter());

        if (!counter.allowRequest(limit)) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write("{\"success\":false,\"message\":\"Rate limit exceeded. Too many requests, please slow down.\"}");
            return;
        }

        response.setHeader("X-RateLimit-Limit", String.valueOf(limit));
        response.setHeader("X-RateLimit-Remaining", String.valueOf(counter.getRemaining(limit)));

        filterChain.doFilter(request, response);
    }

    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader != null && !xfHeader.isBlank()) {
            return xfHeader.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
