package com.example.daemon.controller;

import com.example.daemon.dto.ControlDaemonRequest;
import com.example.daemon.dto.ControlDaemonResponse;
import com.example.daemon.dto.DaemonAction;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.daemon.service.DaemonService;

@RestController
@RequestMapping("/api/v1/downloads")
public class DownloadController {

    @Autowired
    private DaemonService daemonService;

    @PostMapping("")
    public ResponseEntity<ControlDaemonResponse> controlDaemon(@Valid @RequestBody ControlDaemonRequest request) {
        switch (request.action()) {
            case START:
                boolean started = daemonService.startDaemon(request.target());
                if (started) {
                    return ResponseEntity.ok(new ControlDaemonResponse());
                } else {
                    return ResponseEntity.badRequest().body(new ControlDaemonResponse());
                }
            case STOP:
                if (request.downloadId() == null || request.downloadId().isEmpty()) {
                    return ResponseEntity.badRequest().body(new ControlDaemonResponse());
                }
                boolean stopped = daemonService.stopDaemon();
                if (stopped) {
                    return ResponseEntity.ok(new ControlDaemonResponse());
                } else {
                    return ResponseEntity.badRequest().body(new ControlDaemonResponse());
                }
            default:
                return ResponseEntity.badRequest().body(new ControlDaemonResponse());
        }
    }
}