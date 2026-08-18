package com.letraaletra.api.features.admin.application.output;

public record StorageOutput(
        long used,
        long free,
        long total,
        double usage
) {
}
