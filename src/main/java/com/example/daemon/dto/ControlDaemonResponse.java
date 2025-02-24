package com.example.daemon.dto;

public record ControlDaemonResponse(
    DaemonAction action,
    ControlDaemonResult result,
    String downloadId
) {}