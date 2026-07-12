package com.letraaletra.api.features.admin.infrastructure.service;

import com.letraaletra.api.features.admin.application.port.MeterChecker;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;

@Service
public class MeterCheckerImp implements MeterChecker {
    private final MeterRegistry meterRegistry;

    public MeterCheckerImp(
            MeterRegistry meterRegistry
    ) {
        this.meterRegistry = meterRegistry;
    }

    @Override
    public Gauge find(String name) {
        return meterRegistry.find(name)
                .gauge();
    }
}
