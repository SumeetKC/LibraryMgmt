package com.library.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.library.entity.User;
import com.library.service.UserDetailsServiceImpl;

@RestController
@RequestMapping("/api/v2/users")
public class UserControllerV2 {

    private final UserDetailsServiceImpl userDetailsServiceImpl;

    public UserControllerV2(UserDetailsServiceImpl userDetailsServiceImpl) {
        this.userDetailsServiceImpl = userDetailsServiceImpl;
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        // Implementation to retrieve all users
        return ResponseEntity.ok().body(userDetailsServiceImpl.getAllUsers());
    }

}
