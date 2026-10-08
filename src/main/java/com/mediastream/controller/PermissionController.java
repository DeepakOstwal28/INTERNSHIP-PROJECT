package com.mediastream.controller;

import com.mediastream.dto.*;
import com.mediastream.entity.PaymentTransaction;
import com.mediastream.entity.Permission;
import com.mediastream.service.PermissionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/permissions")
public class PermissionController {

    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Permission>>> getAllPermissions() {
        List<Permission> list = permissionService.getAllPermissions();
        return ResponseEntity.ok(ApiResponse.ok("Permissions retrieved successfully", list));
    }

    @PostMapping("/request-access")
    public ResponseEntity<ApiResponse<AccessRequestDto>> requestAccess(
            @Valid @RequestBody CreateAccessRequestDto dto,
            Authentication authentication) {
        AccessRequestDto response = permissionService.createAccessRequest(dto, authentication.getName());
        return ResponseEntity.ok(ApiResponse.ok("Access request submitted to admin successfully", response));
    }

    @GetMapping("/my-requests")
    public ResponseEntity<ApiResponse<List<AccessRequestDto>>> getMyRequests(Authentication authentication) {
        List<AccessRequestDto> list = permissionService.getMyRequests(authentication.getName());
        return ResponseEntity.ok(ApiResponse.ok("User access requests retrieved", list));
    }

    @PostMapping("/purchase")
    public ResponseEntity<ApiResponse<PaymentTransaction>> purchasePermission(
            @Valid @RequestBody PurchasePermissionDto dto,
            Authentication authentication) {
        PaymentTransaction payment = permissionService.purchasePermission(dto, authentication.getName());
        return ResponseEntity.ok(ApiResponse.ok("Payment processed and permission granted successfully!", payment));
    }
}
