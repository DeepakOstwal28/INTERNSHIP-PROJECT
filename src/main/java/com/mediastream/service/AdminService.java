package com.mediastream.service;

import com.mediastream.dto.AccessRequestDto;
import com.mediastream.dto.AdminStatsDto;
import com.mediastream.dto.UserDto;
import com.mediastream.entity.AccessRequest;
import com.mediastream.entity.Permission;
import com.mediastream.entity.Role;
import com.mediastream.entity.User;
import com.mediastream.entity.UserPermissionGrant;
import com.mediastream.exception.ApiException;
import com.mediastream.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserPermissionGrantRepository grantRepository;
    private final AccessRequestRepository accessRequestRepository;
    private final MediaContentRepository mediaRepository;
    private final PaymentTransactionRepository paymentRepository;

    public AdminService(UserRepository userRepository,
                        RoleRepository roleRepository,
                        PermissionRepository permissionRepository,
                        UserPermissionGrantRepository grantRepository,
                        AccessRequestRepository accessRequestRepository,
                        MediaContentRepository mediaRepository,
                        PaymentTransactionRepository paymentRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.grantRepository = grantRepository;
        this.accessRequestRepository = accessRequestRepository;
        this.mediaRepository = mediaRepository;
        this.paymentRepository = paymentRepository;
    }

    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream().map(this::toUserDto).toList();
    }

    @Transactional(readOnly = true)
    public List<String> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(Role::getName)
                .sorted()
                .toList();
    }

    @Transactional
    public UserDto assignRolesToUser(Long userId, java.util.Set<String> roleNames) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException("User not found with id: " + userId, HttpStatus.NOT_FOUND));

        if (roleNames == null || roleNames.isEmpty()) {
            throw new ApiException("At least one role must be assigned to the user", HttpStatus.BAD_REQUEST);
        }

        java.util.Set<Role> rolesToSet = new java.util.HashSet<>();
        for (String rName : roleNames) {
            String normalized = rName.trim().toUpperCase();
            if (!normalized.startsWith("ROLE_")) {
                normalized = "ROLE_" + normalized;
            }
            final String finalRoleName = normalized;
            Role role = roleRepository.findByName(finalRoleName)
                    .orElseGet(() -> roleRepository.save(new Role(finalRoleName)));
            rolesToSet.add(role);
        }

        user.setRoles(rolesToSet);
        User savedUser = userRepository.save(user);
        return toUserDto(savedUser);
    }

    @Transactional
    public UserDto addRoleToUser(Long userId, String roleName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException("User not found with id: " + userId, HttpStatus.NOT_FOUND));

        String normalized = roleName.trim().toUpperCase();
        if (!normalized.startsWith("ROLE_")) {
            normalized = "ROLE_" + normalized;
        }
        final String finalRoleName = normalized;
        Role role = roleRepository.findByName(finalRoleName)
                .orElseGet(() -> roleRepository.save(new Role(finalRoleName)));

        user.getRoles().add(role);
        User savedUser = userRepository.save(user);
        return toUserDto(savedUser);
    }

    @Transactional
    public UserDto removeRoleFromUser(Long userId, String roleName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException("User not found with id: " + userId, HttpStatus.NOT_FOUND));

        String normalized = roleName.trim().toUpperCase();
        if (!normalized.startsWith("ROLE_")) {
            normalized = "ROLE_" + normalized;
        }
        final String finalRoleName = normalized;

        user.getRoles().removeIf(r -> r.getName().equalsIgnoreCase(finalRoleName));
        if (user.getRoles().isEmpty()) {
            Role roleUser = roleRepository.findByName("ROLE_USER")
                    .orElseGet(() -> roleRepository.save(new Role("ROLE_USER")));
            user.getRoles().add(roleUser);
        }

        User savedUser = userRepository.save(user);
        return toUserDto(savedUser);
    }

    public UserDto toUserDto(User u) {
        UserDto dto = new UserDto();
        dto.setId(u.getId());
        dto.setUsername(u.getUsername());
        dto.setEmail(u.getEmail());
        dto.setEnabled(u.isEnabled());
        dto.setCreatedAt(u.getCreatedAt());
        dto.setRoles(u.getRoles().stream().map(Role::getName).sorted().toList());

        List<UserDto.ActivePermissionDto> perms = u.getPermissionGrants().stream()
                .filter(UserPermissionGrant::isActive)
                .map(g -> new UserDto.ActivePermissionDto(
                        g.getPermission().getId(),
                        g.getPermission().getName(),
                        g.getPermission().getDescription(),
                        g.getExpiresAt(),
                        g.getExpiresAt() != null,
                        g.getGrantType()
                ))
                .toList();
        dto.setActivePermissions(perms);
        return dto;
    }

    @Transactional
    public void grantPermissionToUser(Long userId, Long permissionId, Integer hours) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException("User not found with id: " + userId, HttpStatus.NOT_FOUND));

        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new ApiException("Permission not found with id: " + permissionId, HttpStatus.NOT_FOUND));

        grantRepository.deleteByUserIdAndPermissionId(userId, permissionId);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = (hours != null && hours > 0) ? now.plusHours(hours) : null;
        String grantType = (hours != null && hours > 0) ? "ADMIN_TEMP_GRANT" : "ADMIN_PERMANENT_GRANT";

        UserPermissionGrant grant = new UserPermissionGrant(user, permission, now, expiresAt, grantType);
        grantRepository.save(grant);
    }

    @Transactional
    public void revokePermissionFromUser(Long userId, Long permissionId) {
        grantRepository.deleteByUserIdAndPermissionId(userId, permissionId);
    }

    @Transactional(readOnly = true)
    public List<AccessRequestDto> getAllRequests() {
        return accessRequestRepository.findAllByOrderByCreatedAtDesc()
                .stream().map(this::toRequestDto).toList();
    }

    @Transactional
    public AccessRequestDto approveRequest(Long requestId, String adminNotes) {
        AccessRequest request = accessRequestRepository.findById(requestId)
                .orElseThrow(() -> new ApiException("Access request not found with id: " + requestId, HttpStatus.NOT_FOUND));

        if (!"PENDING".equalsIgnoreCase(request.getStatus())) {
            throw new ApiException("Request has already been processed: " + request.getStatus(), HttpStatus.BAD_REQUEST);
        }

        request.setStatus("APPROVED");
        request.setAdminNotes(adminNotes != null ? adminNotes : "Approved by Admin");
        request.setReviewedAt(LocalDateTime.now());
        AccessRequest savedRequest = accessRequestRepository.save(request);

        // Automatically grant temporary hours access
        grantPermissionToUser(request.getUser().getId(), request.getPermission().getId(), request.getRequestedHours());

        return toRequestDto(savedRequest);
    }

    @Transactional
    public AccessRequestDto rejectRequest(Long requestId, String adminNotes) {
        AccessRequest request = accessRequestRepository.findById(requestId)
                .orElseThrow(() -> new ApiException("Access request not found with id: " + requestId, HttpStatus.NOT_FOUND));

        if (!"PENDING".equalsIgnoreCase(request.getStatus())) {
            throw new ApiException("Request has already been processed: " + request.getStatus(), HttpStatus.BAD_REQUEST);
        }

        request.setStatus("REJECTED");
        request.setAdminNotes(adminNotes != null ? adminNotes : "Rejected by Admin");
        request.setReviewedAt(LocalDateTime.now());
        AccessRequest savedRequest = accessRequestRepository.save(request);

        return toRequestDto(savedRequest);
    }

    @Transactional(readOnly = true)
    public AdminStatsDto getStats() {
        long totalUsers = userRepository.count();
        long pendingRequests = accessRequestRepository.countByStatus("PENDING");
        long totalMedia = mediaRepository.count();
        long activeGrants = grantRepository.count();
        Double totalRev = paymentRepository.calculateTotalRevenue();

        return new AdminStatsDto(
                totalUsers,
                pendingRequests,
                totalMedia,
                activeGrants,
                totalRev != null ? totalRev : 0.0
        );
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
