package com.letraaletra.api.features.admin.application.usecase;

import com.letraaletra.api.features.admin.application.output.GetSystemStatusOutput;
import com.letraaletra.api.features.admin.application.port.HealthChecker;
import com.letraaletra.api.features.admin.application.port.MeterChecker;
import com.letraaletra.api.features.admin.application.service.GetSystemStatusService;
import io.micrometer.core.instrument.Gauge;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetSystemStatusServiceTest {

    @Mock
    private MeterChecker meterChecker;

    @Mock
    private HealthChecker healthChecker;

    @Mock
    private Gauge systemCpuGauge;

    @Mock
    private Gauge processCpuGauge;

    @Mock
    private Gauge memoryUsedGauge;

    @Mock
    private Gauge memoryMaxGauge;

    @Mock
    private Gauge diskFreeGauge;

    @Mock
    private Gauge diskTotalGauge;

    @Mock
    private Gauge uptimeGauge;

    private GetSystemStatusService service;

    @BeforeEach
    void setUp() {
        service = new GetSystemStatusService(meterChecker, healthChecker);

        lenient().when(meterChecker.find(anyString())).thenReturn(null);
        lenient().when(healthChecker.getStatus()).thenReturn("UNKNOWN");
    }

    @Test
    @DisplayName("Should return full system status successfully when all metrics and health are available")
    void shouldReturnSystemStatusSuccessfully() {
        when(healthChecker.getStatus()).thenReturn("UP");

        when(meterChecker.find("system.cpu.usage")).thenReturn(systemCpuGauge);
        when(systemCpuGauge.value()).thenReturn(0.45);

        when(meterChecker.find("process.cpu.usage")).thenReturn(processCpuGauge);
        when(processCpuGauge.value()).thenReturn(0.15);

        when(meterChecker.find("jvm.memory.used")).thenReturn(memoryUsedGauge);
        when(memoryUsedGauge.value()).thenReturn(512.0);

        when(meterChecker.find("jvm.memory.max")).thenReturn(memoryMaxGauge);
        when(memoryMaxGauge.value()).thenReturn(2048.0);

        when(meterChecker.find("disk.free")).thenReturn(diskFreeGauge);
        when(diskFreeGauge.value()).thenReturn(300.0);

        when(meterChecker.find("disk.total")).thenReturn(diskTotalGauge);
        when(diskTotalGauge.value()).thenReturn(1000.0);

        when(meterChecker.find("process.uptime")).thenReturn(uptimeGauge);
        when(uptimeGauge.value()).thenReturn(3600.0);

        GetSystemStatusOutput output = service.handle();

        assertNotNull(output);
        assertEquals(3600L, output.uptime());
        assertEquals("UP", output.health());

        assertEquals(0.45, output.cpu().systemUsage());
        assertEquals(0.15, output.cpu().processUsage());

        assertEquals(512L, output.memory().used());
        assertEquals(2048L, output.memory().max());
        assertEquals(25.0, output.memory().usage());

        assertEquals(700L, output.storage().used());
        assertEquals(300L, output.storage().free());
        assertEquals(1000L, output.storage().total());
        assertEquals(70.0, output.storage().usage());
    }

    @Test
    @DisplayName("Should return zero values safely when all metric gauges are null")
    void shouldReturnZeroValuesWhenGaugesAreNull() {
        when(healthChecker.getStatus()).thenReturn("UNKNOWN");

        GetSystemStatusOutput output = service.handle();

        assertNotNull(output);
        assertEquals(0L, output.uptime());
        assertEquals("UNKNOWN", output.health());

        assertEquals(0.0, output.cpu().systemUsage());
        assertEquals(0.0, output.cpu().processUsage());

        assertEquals(0L, output.memory().used());
        assertEquals(0L, output.memory().max());
        assertEquals(0.0, output.memory().usage());

        assertEquals(0L, output.storage().used());
        assertEquals(0L, output.storage().free());
        assertEquals(0L, output.storage().total());
        assertEquals(0.0, output.storage().usage());
    }

    @Test
    @DisplayName("Should prevent Division by Zero and return 0% when memory max metric is zero")
    void shouldReturnZeroPercentageWhenMemoryMaxIsZero() {
        when(healthChecker.getStatus()).thenReturn("UP");

        when(meterChecker.find("jvm.memory.used")).thenReturn(memoryUsedGauge);
        when(memoryUsedGauge.value()).thenReturn(100.0);

        when(meterChecker.find("jvm.memory.max")).thenReturn(memoryMaxGauge);
        when(memoryMaxGauge.value()).thenReturn(0.0);

        GetSystemStatusOutput output = service.handle();

        assertEquals(100L, output.memory().used());
        assertEquals(0L, output.memory().max());
        assertEquals(0.0, output.memory().usage());
    }

    @Test
    @DisplayName("Should prevent Division by Zero and return 0% when disk total metric is zero")
    void shouldReturnZeroPercentageWhenDiskTotalIsZero() {
        when(healthChecker.getStatus()).thenReturn("UP");

        when(meterChecker.find("disk.free")).thenReturn(diskFreeGauge);
        when(diskFreeGauge.value()).thenReturn(50.0);

        when(meterChecker.find("disk.total")).thenReturn(diskTotalGauge);
        when(diskTotalGauge.value()).thenReturn(0.0);

        GetSystemStatusOutput output = service.handle();

        assertEquals(0L, output.storage().total());
        assertEquals(50L, output.storage().free());
        assertEquals(0L, output.storage().used());
        assertEquals(0.0, output.storage().usage());
    }

    @Test
    @DisplayName("Should handle edge cases with negative metric values safely without crashing")
    void shouldHandleNegativeMetricValues() {
        when(healthChecker.getStatus()).thenReturn("DOWN");

        when(meterChecker.find("process.uptime")).thenReturn(uptimeGauge);
        when(uptimeGauge.value()).thenReturn(-10.0);

        when(meterChecker.find("system.cpu.usage")).thenReturn(systemCpuGauge);
        when(systemCpuGauge.value()).thenReturn(-0.5);

        GetSystemStatusOutput output = service.handle();

        assertEquals(-10L, output.uptime());
        assertEquals(-0.5, output.cpu().systemUsage());
        assertEquals("DOWN", output.health());
    }

    @Test
    @DisplayName("Should handle fractional casting values correctly down to long values")
    void shouldTruncateFloatingPointValuesToLong() {
        when(healthChecker.getStatus()).thenReturn("UP");

        when(meterChecker.find("process.uptime")).thenReturn(uptimeGauge);
        when(uptimeGauge.value()).thenReturn(1234.567);

        when(meterChecker.find("jvm.memory.used")).thenReturn(memoryUsedGauge);
        when(memoryUsedGauge.value()).thenReturn(500.99);

        when(meterChecker.find("jvm.memory.max")).thenReturn(memoryMaxGauge);
        when(memoryMaxGauge.value()).thenReturn(1000.0);

        GetSystemStatusOutput output = service.handle();

        assertEquals(1234L, output.uptime());
        assertEquals(500L, output.memory().used());
    }

    @Test
    @DisplayName("Storage used should not be negative when disk free exceeds total")
    void shouldNotReturnNegativeStorageUsedWhenFreeIsGreaterThanTotal() {
        when(meterChecker.find("disk.free")).thenReturn(diskFreeGauge);
        when(diskFreeGauge.value()).thenReturn(150.0);

        when(meterChecker.find("disk.total")).thenReturn(diskTotalGauge);
        when(diskTotalGauge.value()).thenReturn(100.0);

        GetSystemStatusOutput output = service.handle();

        assertTrue(output.storage().used() >= 0, "Storage used should be guarded against negative numbers");
    }

    @Test
    @DisplayName("Memory usage percentage should not exceed 100% due to telemetry latency")
    void jvmMemoryPercentageShouldBeBoundedToMaximumOneHundredPercent() {
        when(meterChecker.find("jvm.memory.used")).thenReturn(memoryUsedGauge);
        when(memoryUsedGauge.value()).thenReturn(1050.0);

        when(meterChecker.find("jvm.memory.max")).thenReturn(memoryMaxGauge);
        when(memoryMaxGauge.value()).thenReturn(1000.0);

        GetSystemStatusOutput output = service.handle();

        assertTrue(output.memory().usage() <= 100.0, "Percentage should be capped at 100%");
    }
}