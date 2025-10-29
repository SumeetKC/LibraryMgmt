package com.library.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.library.entity.User;
import com.library.service.UserDetailsServiceImpl;

import jakarta.validation.Valid;

@RestController
public class HelloController {

    private final UserDetailsServiceImpl userDetailsServiceImpl;

    public HelloController(UserDetailsServiceImpl userDetailsServiceImpl) {
        this.userDetailsServiceImpl = userDetailsServiceImpl;
    }
    
    @GetMapping("/hello")
    public String hello() {
        return "Hello, Library!";
    }

    @PostMapping("/create-user")
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        // Implementation to create a new user
        return ResponseEntity.ok().body(userDetailsServiceImpl.saveUser(user));
    }

}
