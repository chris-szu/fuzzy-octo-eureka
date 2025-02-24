package com.example.daemon.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import org.springframework.lang.Nullable;

public record ControlDaemonRequest (
    @NotNull(message = "Action is required")
    DaemonAction action, // todo make it accept any letter casing

    @Nullable
    @JsonProperty("download_id")
    String downloadId // Optional
) {}