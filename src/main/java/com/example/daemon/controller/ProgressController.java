package com.example.daemon.controller;

import com.example.daemon.service.DaemonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/progress")
public class ProgressController {

    @Autowired
    private DaemonService daemonService;

    @GetMapping("/")
    public ResponseEntity<String> startDaemon() {
        boolean started = daemonService.startDaemon();
        if (started) {
            return ResponseEntity.ok("Daemon started successfully");
        } else {
            return ResponseEntity.badRequest().body("Daemon is already running");
        }
    }
}