package com.example.daemon.controller;

import com.example.daemon.dto.ControlDaemonRequest;
import com.example.daemon.dto.ControlDaemonResponse;
import com.example.daemon.dto.StartDaemonResult;
import com.example.daemon.dto.StopDaemonResult;
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

    // todo would be nice to have openapi docs
    @PostMapping("")
    public ResponseEntity<ControlDaemonResponse> controlDaemon(@Valid @RequestBody ControlDaemonRequest request) {
        switch (request.action()) {
            case START:
                StartDaemonResult startResult = daemonService.startDaemon();
                ControlDaemonResponse startResponse = new ControlDaemonResponse(request.action(), startResult, null);
                return switch (startResult) {
                    case STARTING, ALREADY_STARTED -> ResponseEntity.ok(startResponse);
                    default -> ResponseEntity.internalServerError().body(startResponse);
                };
            case STOP:
                StopDaemonResult stopResult = daemonService.stopDaemon();
                ControlDaemonResponse stopResponse = new ControlDaemonResponse(request.action(), stopResult, null);
                return switch (stopResult) {
                    case STOPPING, ALREADY_STOPPED -> ResponseEntity.ok(stopResponse);
                    default -> ResponseEntity.internalServerError().body(stopResponse);
                };
            default: // todo better response msg
                return ResponseEntity.badRequest().body(new ControlDaemonResponse(request.action(), null, null));
        }
    }
}