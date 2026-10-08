package com.mediastream.dto;

import jakarta.validation.constraints.NotNull;

public class PurchasePermissionDto {
    @NotNull(message = "Permission ID is required")
    private Long permissionId;

    private String paymentMethod = "CARD"; // CARD, UPI, PAYPAL, SIMULATED

    public PurchasePermissionDto() {}

    public PurchasePermissionDto(Long permissionId, String paymentMethod) {
        this.permissionId = permissionId;
        this.paymentMethod = paymentMethod;
    }

    public Long getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(Long permissionId) {
        this.permissionId = permissionId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
