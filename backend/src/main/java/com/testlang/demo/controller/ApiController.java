package com.testlang.demo.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiController {

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");
        
        Map<String, Object> response = new HashMap<>();
        
        if ("admin".equals(username) && "1234".equals(password)) {
            response.put("token", "jwt-token-12345");
            response.put("user", username);
            response.put("success", true);
            
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "application/json");
            headers.add("X-App", "TestLangDemo");
            
            return ResponseEntity.ok().headers(headers).body(response);
        } else {
            response.put("error", "Invalid credentials");
            response.put("success", false);
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<Map<String, Object>> getUser(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", Integer.parseInt(id));
        response.put("username", "admin");
        response.put("email", "admin@testlang.com");
        response.put("role", "USER");
        
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        headers.add("X-App", "TestLangDemo");
        
        return ResponseEntity.ok().headers(headers).body(response);
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<Map<String, Object>> updateUser(@PathVariable String id, @RequestBody Map<String, String> updateRequest) {
        String role = updateRequest.get("role");
        
        Map<String, Object> response = new HashMap<>();
        response.put("id", Integer.parseInt(id));
        response.put("username", "admin");
        response.put("email", "admin@testlang.com");
        response.put("role", role);
        response.put("updated", true);
        
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        headers.add("X-App", "TestLangDemo");
        
        return ResponseEntity.ok().headers(headers).body(response);
    }
}