package com.example.daemon.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/progress")
public class ProgressController {

    @GetMapping("/")
    public ResponseEntity<String> getProgress() {
        return ResponseEntity.ok("get process");
    }
}