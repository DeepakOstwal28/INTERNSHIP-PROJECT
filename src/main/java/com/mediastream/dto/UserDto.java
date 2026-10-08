package com.mediastream.dto;

import java.time.LocalDateTime;
import java.util.List;

public class UserDto {
    private Long id;
    private String username;
    private String email;
    private boolean enabled;
    private LocalDateTime createdAt;
    private List<String> roles;
    private List<ActivePermissionDto> activePermissions;

    public static class ActivePermissionDto {
        private Long permissionId;
        private String name;
        private String description;
        private LocalDateTime expiresAt;
        private boolean isTemporary;
        private String grantType;

        public ActivePermissionDto() {}

        public ActivePermissionDto(Long permissionId, String name, String description, LocalDateTime expiresAt, boolean isTemporary, String grantType) {
            this.permissionId = permissionId;
            this.name = name;
            this.description = description;
            this.expiresAt = expiresAt;
            this.isTemporary = isTemporary;
            this.grantType = grantType;
        }

        public Long getPermissionId() {
            return permissionId;
        }

        public void setPermissionId(Long permissionId) {
            this.permissionId = permissionId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public LocalDateTime getExpiresAt() {
            return expiresAt;
        }

        public void setExpiresAt(LocalDateTime expiresAt) {
            this.expiresAt = expiresAt;
        }

        public boolean isTemporary() {
            return isTemporary;
        }

        public void setTemporary(boolean temporary) {
            isTemporary = temporary;
        }

        public String getGrantType() {
            return grantType;
        }

        public void setGrantType(String grantType) {
            this.grantType = grantType;
        }
    }

    public UserDto() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public List<ActivePermissionDto> getActivePermissions() {
        return activePermissions;
    }

    public void setActivePermissions(List<ActivePermissionDto> activePermissions) {
        this.activePermissions = activePermissions;
    }
}
