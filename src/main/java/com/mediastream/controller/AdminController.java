package com.mediastream.controller;

import com.mediastream.dto.AccessRequestDto;
import com.mediastream.dto.AdminStatsDto;
import com.mediastream.dto.ApiResponse;
import com.mediastream.dto.UserDto;
import com.mediastream.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<UserDto>>> getAllUsers() {
        List<UserDto> users = adminService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.ok("All users retrieved", users));
    }

    @PostMapping("/users/{userId}/permissions/{permissionId}/grant")
    public ResponseEntity<ApiResponse<Void>> grantPermission(
            @PathVariable Long userId,
            @PathVariable Long permissionId,
            @RequestParam(required = false, defaultValue = "0") Integer hours) {
        adminService.grantPermissionToUser(userId, permissionId, hours);
        return ResponseEntity.ok(ApiResponse.ok("Permission granted to user successfully"));
    }

    @PostMapping("/users/{userId}/permissions/{permissionId}/revoke")
    public ResponseEntity<ApiResponse<Void>> revokePermission(
            @PathVariable Long userId,
            @PathVariable Long permissionId) {
        adminService.revokePermissionFromUser(userId, permissionId);
        return ResponseEntity.ok(ApiResponse.ok("Permission revoked from user successfully"));
    }

    @GetMapping("/requests")
    public ResponseEntity<ApiResponse<List<AccessRequestDto>>> getAllRequests() {
        List<AccessRequestDto> requests = adminService.getAllRequests();
        return ResponseEntity.ok(ApiResponse.ok("All access requests retrieved", requests));
    }

    @PostMapping("/requests/{requestId}/approve")
    public ResponseEntity<ApiResponse<AccessRequestDto>> approveRequest(
            @PathVariable Long requestId,
            @RequestParam(required = false) String adminNotes) {
        AccessRequestDto approved = adminService.approveRequest(requestId, adminNotes);
        return ResponseEntity.ok(ApiResponse.ok("Access request approved and temporary access granted", approved));
    }

    @PostMapping("/requests/{requestId}/reject")
    public ResponseEntity<ApiResponse<AccessRequestDto>> rejectRequest(
            @PathVariable Long requestId,
            @RequestParam(required = false) String adminNotes) {
        AccessRequestDto rejected = adminService.rejectRequest(requestId, adminNotes);
        return ResponseEntity.ok(ApiResponse.ok("Access request rejected", rejected));
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<AdminStatsDto>> getStats() {
        AdminStatsDto stats = adminService.getStats();
        return ResponseEntity.ok(ApiResponse.ok("Admin stats retrieved", stats));
    }
}
