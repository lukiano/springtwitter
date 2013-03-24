package com.lucho.controller;

import com.lucho.domain.User;
import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class UserRegistrationForm {

    @NotNull
    @Size(min = User.MIN_USER_LENGTH, max = User.MAX_USER_LENGTH)
    private String username;

    @NotNull
    @Size(min = User.MIN_PASS_LENGTH, max = User.MAX_PASS_LENGTH)
    private String password;

    @NotNull
    @Email
    private String email;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
