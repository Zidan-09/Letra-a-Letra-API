package com.letraaletra.api.features.admin.application.port;

import io.micrometer.core.instrument.Gauge;

public interface MeterChecker {
    Gauge find(String name);
}
