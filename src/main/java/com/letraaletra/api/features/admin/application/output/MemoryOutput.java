package com.letraaletra.api.features.admin.application.output;

public record MemoryOutput(
        long used,
        long max,
        double usage
) {
}
