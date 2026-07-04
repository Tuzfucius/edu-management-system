package com.tzufucius.edu.edumanagementsystem.dto;

public class LoginUserVO {
    private Long id;
    private String username;
    private String displayName;
    private String role;

    public LoginUserVO() {
    }

    public LoginUserVO(Long id, String username, String displayName, String role) {
        this.id = id;
        this.username = username;
        this.displayName = displayName;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getRole() {
        return role;
    }
}