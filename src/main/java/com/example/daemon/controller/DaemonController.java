package com.example.daemon.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.daemon.service.DaemonService;

@RestController
@RequestMapping("/api/daemon")
public class DaemonController {

    @Autowired
    private DaemonService daemonService;

    @PostMapping("/start")
    public ResponseEntity<String> startDaemon() {
        boolean started = daemonService.startDaemon();
        if (started) {
            return ResponseEntity.ok("Daemon started successfully");
        } else {
            return ResponseEntity.badRequest().body("Daemon is already running");
        }
    }

    @PostMapping("/stop")
    public ResponseEntity<String> stopDaemon() {
        boolean stopped = daemonService.stopDaemon();
        if (stopped) {
            return ResponseEntity.ok("Daemon stopped successfully");
        } else {
            return ResponseEntity.badRequest().body("Failed to stop daemon or daemon was not running");
        }
    }

    @GetMapping("/status")
    public ResponseEntity<String> getDaemonStatus() {
        boolean isRunning = daemonService.isRunning();
        return ResponseEntity.ok(isRunning ? "Daemon is running" : "Daemon is stopped");
    }
}