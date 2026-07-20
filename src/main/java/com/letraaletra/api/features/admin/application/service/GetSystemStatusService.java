package com.letraaletra.api.features.admin.application.service;

import com.letraaletra.api.features.admin.application.output.CpuOutput;
import com.letraaletra.api.features.admin.application.output.GetSystemStatusOutput;
import com.letraaletra.api.features.admin.application.output.MemoryOutput;
import com.letraaletra.api.features.admin.application.output.StorageOutput;
import com.letraaletra.api.features.admin.application.port.HealthChecker;
import com.letraaletra.api.features.admin.application.port.MeterChecker;
import io.micrometer.core.instrument.Gauge;

public class GetSystemStatusService {
    private final MeterChecker meterChecker;
    private final HealthChecker healthChecker;

    public GetSystemStatusService(
            MeterChecker meterChecker,
            HealthChecker healthChecker
    ) {
        this.meterChecker = meterChecker;
        this.healthChecker = healthChecker;
    }

    public GetSystemStatusOutput handle() {
        String health = healthChecker.getStatus();

        double systemCpuUsage = metric("system.cpu.usage");
        double processCpuUsage = metric("process.cpu.usage");

        double memoryUsed = metric("jvm.memory.used");
        double memoryMax = metric("jvm.memory.max");

        double diskFree = metric("disk.free");
        double diskTotal = metric("disk.total");

        double uptime = metric("process.uptime");

        return buildOutput(
                uptime,
                systemCpuUsage,
                processCpuUsage,
                memoryUsed,
                memoryMax,
                diskFree,
                diskTotal,
                health
        );
    }

    private double metric(String name) {
        Gauge gauge = meterChecker.find(name);

        return gauge == null
                ? 0
                : gauge.value();
    }

    private double percentage(double used, double total) {
        if (Double.isNaN(total)
                || Double.isInfinite(total)
                || total <= 0) {
            return 0;
        }

        double percentage = (used / total) * 100;

        if (Double.isNaN(percentage) || Double.isInfinite(percentage)) {
            return 0;
        }

        return Math.clamp(percentage, 0, 100);
    }

    private GetSystemStatusOutput buildOutput(
            double uptime,
            double systemCpuUsage,
            double processCpuUsage,
            double memoryUsed,
            double memoryMax,
            double diskFree,
            double diskTotal,
            String health
    ) {

        long totalStorage = (long) diskTotal;
        long freeStorage = (long) diskFree;
        long usedStorage = Math.max(0, totalStorage - freeStorage);

        return new GetSystemStatusOutput(
                (long) uptime,
                new CpuOutput(
                        systemCpuUsage,
                        processCpuUsage
                ),
                new MemoryOutput(
                        (long) memoryUsed,
                        (long) memoryMax,
                        percentage(memoryUsed, memoryMax)
                ),
                new StorageOutput(
                        usedStorage,
                        freeStorage,
                        totalStorage,
                        percentage(usedStorage, diskTotal)
                ),
                health
        );
    }
}
