package com.assetmanager.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "users")
public class User {

    @Id // Primary Key
    @NotNull(message = "User ID is mandatory")
    private int userId;

    @NotBlank(message = "Username is mandatory")
    private String username;

    @NotBlank(message = "Employee ID is mandatory")
    private String employeeId;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonManagedReference
    @JsonIgnore
    private List<Device> devices;

    protected User() {}

    @JsonCreator
    public User(
        @JsonProperty("userId") int userId,
        @JsonProperty("username") String username,
        @JsonProperty("employeeId") String employeeId
    ){
        this.userId = userId;
        this.username = username;
        this.employeeId = employeeId;
    }

    public String getUsername() {
        return username;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public int getUserId() {
        return userId;
    }

    public List<Device> getDevices() {
        return devices;
    }
}
