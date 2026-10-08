package com.mediastream.dto;

public class AdminStatsDto {
    private long totalUsers;
    private long pendingRequests;
    private long totalMedia;
    private long activeGrants;
    private double totalRevenue;

    public AdminStatsDto() {}

    public AdminStatsDto(long totalUsers, long pendingRequests, long totalMedia, long activeGrants, double totalRevenue) {
        this.totalUsers = totalUsers;
        this.pendingRequests = pendingRequests;
        this.totalMedia = totalMedia;
        this.activeGrants = activeGrants;
        this.totalRevenue = totalRevenue;
    }

    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public long getPendingRequests() {
        return pendingRequests;
    }

    public void setPendingRequests(long pendingRequests) {
        this.pendingRequests = pendingRequests;
    }

    public long getTotalMedia() {
        return totalMedia;
    }

    public void setTotalMedia(long totalMedia) {
        this.totalMedia = totalMedia;
    }

    public long getActiveGrants() {
        return activeGrants;
    }

    public void setActiveGrants(long activeGrants) {
        this.activeGrants = activeGrants;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }
}
