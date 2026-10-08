package com.mediastream.controller;

import com.mediastream.dto.ApiResponse;
import com.mediastream.dto.MediaDto;
import com.mediastream.service.MediaService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/media")
public class MediaController {

    private final MediaService mediaService;

    public MediaController(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MediaDto>>> getAllMedia(Authentication authentication) {
        String username = (authentication != null && authentication.isAuthenticated()) ? authentication.getName() : null;
        List<MediaDto> list = mediaService.getAllMedia(username);
        return ResponseEntity.ok(ApiResponse.ok("Media list retrieved", list));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<ApiResponse<List<MediaDto>>> getByCategory(@PathVariable String category, Authentication authentication) {
        String username = (authentication != null && authentication.isAuthenticated()) ? authentication.getName() : null;
        List<MediaDto> list = mediaService.getMediaByCategory(category, username);
        return ResponseEntity.ok(ApiResponse.ok("Category media list retrieved", list));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MediaDto>> getMediaById(@PathVariable Long id, Authentication authentication) {
        String username = (authentication != null && authentication.isAuthenticated()) ? authentication.getName() : null;
        MediaDto media = mediaService.getMediaById(id, username);
        return ResponseEntity.ok(ApiResponse.ok("Media details retrieved", media));
    }

    @GetMapping("/{id}/stream")
    public ResponseEntity<ApiResponse<MediaDto>> streamMedia(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName();
        MediaDto media = mediaService.streamMedia(id, username);
        return ResponseEntity.ok(ApiResponse.ok("Stream unlocked successfully", media));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<MediaDto>> createMedia(@RequestBody MediaDto dto) {
        MediaDto created = mediaService.createMedia(dto);
        return ResponseEntity.ok(ApiResponse.ok("Media created successfully", created));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteMedia(@PathVariable Long id) {
        mediaService.deleteMedia(id);
        return ResponseEntity.ok(ApiResponse.ok("Media deleted successfully"));
    }
}
