package com.mediastream.service;

import com.mediastream.dto.AccessRequestDto;
import com.mediastream.dto.CreateAccessRequestDto;
import com.mediastream.dto.PurchasePermissionDto;
import com.mediastream.entity.*;
import com.mediastream.exception.ApiException;
import com.mediastream.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PermissionService {

    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;
    private final AccessRequestRepository accessRequestRepository;
    private final UserPermissionGrantRepository grantRepository;
    private final PaymentTransactionRepository paymentRepository;

    public PermissionService(PermissionRepository permissionRepository,
                             UserRepository userRepository,
                             AccessRequestRepository accessRequestRepository,
                             UserPermissionGrantRepository grantRepository,
                             PaymentTransactionRepository paymentRepository) {
        this.permissionRepository = permissionRepository;
        this.userRepository = userRepository;
        this.accessRequestRepository = accessRequestRepository;
        this.grantRepository = grantRepository;
        this.paymentRepository = paymentRepository;
    }

    @Transactional(readOnly = true)
    public List<Permission> getAllPermissions() {
        return permissionRepository.findAll();
    }

    @Transactional
    public AccessRequestDto createAccessRequest(CreateAccessRequestDto req, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException("User not found", HttpStatus.NOT_FOUND));

        Permission permission = permissionRepository.findById(req.getPermissionId())
                .orElseThrow(() -> new ApiException("Permission not found", HttpStatus.NOT_FOUND));

        AccessRequest accessRequest = new AccessRequest(
                user,
                permission,
                req.getRequestedHours(),
                req.getReason()
        );

        AccessRequest saved = accessRequestRepository.save(accessRequest);
        return toRequestDto(saved);
    }

    @Transactional(readOnly = true)
    public List<AccessRequestDto> getMyRequests(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException("User not found", HttpStatus.NOT_FOUND));

        return accessRequestRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream().map(this::toRequestDto).toList();
    }

    @Transactional
    public PaymentTransaction purchasePermission(PurchasePermissionDto dto, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException("User not found", HttpStatus.NOT_FOUND));

        Permission permission = permissionRepository.findById(dto.getPermissionId())
                .orElseThrow(() -> new ApiException("Permission not found", HttpStatus.NOT_FOUND));

        // Create simulated payment transaction
        String txRef = "TX-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        PaymentTransaction payment = new PaymentTransaction(
                user,
                permission,
                permission.getPrice(),
                dto.getPaymentMethod() != null ? dto.getPaymentMethod() : "CARD",
                "SUCCESS",
                txRef
        );
        PaymentTransaction savedPayment = paymentRepository.save(payment);

        // Remove any old/expired grant for this specific permission to cleanly update
        grantRepository.deleteByUserIdAndPermissionId(user.getId(), permission.getId());

        // Grant 30 days of active access
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusDays(30);

        UserPermissionGrant grant = new UserPermissionGrant(
                user,
                permission,
                now,
                expiresAt,
                "PURCHASE"
        );
        grantRepository.save(grant);

        return savedPayment;
    }

    private AccessRequestDto toRequestDto(AccessRequest r) {
        AccessRequestDto dto = new AccessRequestDto();
        dto.setId(r.getId());
        dto.setUserId(r.getUser().getId());
        dto.setUsername(r.getUser().getUsername());
        dto.setUserEmail(r.getUser().getEmail());
        dto.setPermissionId(r.getPermission().getId());
        dto.setPermissionName(r.getPermission().getName());
        dto.setRequestedHours(r.getRequestedHours());
        dto.setReason(r.getReason());
        dto.setStatus(r.getStatus());
        dto.setAdminNotes(r.getAdminNotes());
        dto.setCreatedAt(r.getCreatedAt());
        dto.setReviewedAt(r.getReviewedAt());
        return dto;
    }
}
