package com.letraaletra.api.features.admin.application.output;

public record GetSystemStatusOutput(
    long uptime,
    CpuOutput cpu,
    MemoryOutput memory,
    StorageOutput storage,
    String health
) {
}
