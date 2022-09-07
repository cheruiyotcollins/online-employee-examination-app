package com.example.konvergence.payload;

import com.example.konvergence.model.Role;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

public class UserProfile {
    private Long id;
    private String username;
    private String name;
    private Instant joinedAt;
    private Long questionCount;
    private Long submissionCount;
    private Set<Role> userRoles= new HashSet<>();

    public UserProfile(Long id, String username, String name, Instant joinedAt, Long questionCount, Long submissionCount,  Set<Role> userRoles ) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.joinedAt = joinedAt;
        this.questionCount = questionCount;
        this.submissionCount = submissionCount;
        this.userRoles=userRoles;
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

    public Set<Role> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(Set<Role> userRoles) {
        this.userRoles = userRoles;
    }

    public Instant getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(Instant joinedAt) {
        this.joinedAt = joinedAt;
    }

    public Long getPollCount() {
        return questionCount;
    }

    public void setPollCount(Long questionCount) {
        this.questionCount = questionCount;
    }

    public Long getVoteCount() {
        return submissionCount;
    }

    public void setVoteCount(Long submissionCount) {
        this.submissionCount = submissionCount;
    }
}
