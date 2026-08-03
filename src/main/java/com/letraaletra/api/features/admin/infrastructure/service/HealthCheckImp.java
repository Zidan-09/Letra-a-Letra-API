package com.letraaletra.api.features.admin.infrastructure.service;

import com.letraaletra.api.features.admin.application.port.HealthChecker;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.health.actuate.endpoint.HealthEndpoint;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HealthCheckImp implements HealthChecker {
    private final HealthEndpoint healthEndpoint;

    @Override
    public String getStatus() {
        return healthEndpoint.health()
                .getStatus()
                .getCode();
    }
}
