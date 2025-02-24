package com.example.daemon.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.daemon.service.DaemonService;

@RestController
@RequestMapping("/api/v1/downloads")
public class DownloadController {

    @Autowired
    private DaemonService daemonService;

    @PostMapping("/")
    public ResponseEntity<String> startDaemon() {
        boolean started = daemonService.startDaemon();
        if (started) {
            return ResponseEntity.ok("Daemon started successfully");
        } else {
            return ResponseEntity.badRequest().body("Daemon is already running");
        }
    }
}