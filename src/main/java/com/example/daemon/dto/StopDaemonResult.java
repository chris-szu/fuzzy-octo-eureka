package com.example.daemon.dto;

public enum StopDaemonResult implements ControlDaemonResult {
    STOPPING, ALREADY_STOPPED, FAILED
}
