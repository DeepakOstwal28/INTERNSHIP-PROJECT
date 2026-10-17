package com.mediastream.dto;

import java.util.Set;

public class AssignRoleDto {
    private Set<String> roles;

    public AssignRoleDto() {}

    public AssignRoleDto(Set<String> roles) {
        this.roles = roles;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }
}
