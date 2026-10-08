package com.mediastream.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class CreateAccessRequestDto {
    @NotNull(message = "Permission ID is required")
    private Long permissionId;

    @NotNull(message = "Requested hours is required")
    @Min(value = 1, message = "Minimum requested hours is 1")
    @Max(value = 72, message = "Maximum requested hours is 72")
    private Integer requestedHours;

    private String reason;

    public CreateAccessRequestDto() {}

    public CreateAccessRequestDto(Long permissionId, Integer requestedHours, String reason) {
        this.permissionId = permissionId;
        this.requestedHours = requestedHours;
        this.reason = reason;
    }

    public Long getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(Long permissionId) {
        this.permissionId = permissionId;
    }

    public Integer getRequestedHours() {
        return requestedHours;
    }

    public void setRequestedHours(Integer requestedHours) {
        this.requestedHours = requestedHours;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
