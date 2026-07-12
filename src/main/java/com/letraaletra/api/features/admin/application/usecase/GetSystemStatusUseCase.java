package com.letraaletra.api.features.admin.application.usecase;

import com.letraaletra.api.features.admin.application.input.GetSystemStatusInput;
import com.letraaletra.api.features.admin.application.output.CpuOutput;
import com.letraaletra.api.features.admin.application.output.GetSystemStatusOutput;
import com.letraaletra.api.features.admin.application.output.MemoryOutput;
import com.letraaletra.api.features.admin.application.output.StorageOutput;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.application.usecase.UseCase;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.health.actuate.endpoint.HealthEndpoint;

public class GetSystemStatusUseCase implements UseCase<GetSystemStatusInput, GetSystemStatusOutput> {
    private final MeterRegistry meterRegistry;
    private final HealthEndpoint healthEndpoint;
    private final AdminChecker adminChecker;

    public GetSystemStatusUseCase(
            MeterRegistry meterRegistry,
            HealthEndpoint healthEndpoint,
            AdminChecker adminChecker
    ) {
        this.meterRegistry = meterRegistry;
        this.healthEndpoint = healthEndpoint;
        this.adminChecker = adminChecker;
    }

    @Override
    public GetSystemStatusOutput execute(GetSystemStatusInput input) {
        adminChecker.check(input.principal().auth());

        String health = healthEndpoint.health()
                .getStatus()
                .getCode();

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
        Gauge gauge = meterRegistry.find(name).gauge();

        return gauge == null
                ? 0
                : gauge.value();
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
        long usedStorage = totalStorage - freeStorage;

        return new GetSystemStatusOutput(
                (long) uptime,
                new CpuOutput(
                        systemCpuUsage,
                        processCpuUsage
                ),
                new MemoryOutput(
                        (long) memoryUsed,
                        (long) memoryMax,
                        memoryMax == 0 ?
                                0 :
                                (memoryUsed / memoryMax) * 100
                ),
                new StorageOutput(
                        usedStorage,
                        freeStorage,
                        totalStorage,
                        diskTotal == 0 ?
                                0 :
                                ((diskTotal - diskFree) / diskTotal) * 100
                ),
                health
        );
    }
}
