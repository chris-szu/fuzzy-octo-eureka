package com.example.daemon.dto;

public enum StartDaemonResult implements ControlDaemonResult {
    STARTING, ALREADY_STARTED, FAILED
}
