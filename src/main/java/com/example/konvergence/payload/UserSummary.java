package com.example.konvergence.payload;

public class UserSummary {
    private Long id;
    private String username;
    private String name;
    private Long roleId;

    public UserSummary(Long id, String username, String name,Long roleId) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.roleId=roleId;
    }

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }
}
