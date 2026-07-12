package com.letraaletra.api.features.admin.infrastructure.presentation.dto.response;

import com.letraaletra.api.features.admin.application.output.CpuOutput;
import com.letraaletra.api.features.admin.application.output.MemoryOutput;
import com.letraaletra.api.features.admin.application.output.StorageOutput;

public record GetSystemStatusResponse(
    long uptime,
    CpuOutput cpu,
    MemoryOutput memory,
    StorageOutput storage,
    String health
) {
}
