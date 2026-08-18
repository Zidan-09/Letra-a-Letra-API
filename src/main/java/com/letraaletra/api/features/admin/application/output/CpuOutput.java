package com.letraaletra.api.features.admin.application.output;

public record CpuOutput(
        double systemUsage,
        double processUsage
) {
}
