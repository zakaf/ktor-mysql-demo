package io.zakaf

import io.micrometer.core.instrument.binder.jvm.*
import io.micrometer.core.instrument.binder.system.FileDescriptorMetrics
import io.micrometer.core.instrument.binder.system.ProcessorMetrics
import io.micrometer.core.instrument.binder.system.UptimeMetrics
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig
import java.time.Duration

fun distributionStatisticsConfig(): DistributionStatisticConfig = DistributionStatisticConfig.Builder()
    .percentilesHistogram(true)
    .maximumExpectedValue(Duration.ofSeconds(3).toNanos().toDouble())
    .serviceLevelObjectives(
        Duration.ofMillis(100).toNanos().toDouble(),
        Duration.ofMillis(500).toNanos().toDouble(),
        Duration.ofMillis(1000).toNanos().toDouble()
    )
    .build()

fun jvmMeterBinders() = listOf(
    ClassLoaderMetrics(),
    JvmCompilationMetrics(),
    JvmGcMetrics(),
    JvmHeapPressureMetrics(),
    JvmMemoryMetrics(),
    JvmThreadMetrics(),
)

fun hostMetrics() = listOf(
    ProcessorMetrics(),
    UptimeMetrics(),
    FileDescriptorMetrics()
)
