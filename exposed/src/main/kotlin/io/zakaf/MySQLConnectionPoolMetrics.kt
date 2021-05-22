package io.zakaf

import com.zaxxer.hikari.HikariDataSource
import io.micrometer.core.instrument.FunctionCounter
import io.micrometer.core.instrument.Gauge
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.binder.BaseUnits
import io.micrometer.core.instrument.binder.MeterBinder

class MySQLConnectionPoolMetrics(
    private val dataSource: HikariDataSource
) : MeterBinder {
    override fun bindTo(registry: MeterRegistry) {
        Gauge.builder(
            "connections.in_use", dataSource.hikariPoolMXBean
        ) { obj -> obj.activeConnections.toDouble() }
            .description("The number of connections in use")
            .baseUnit(BaseUnits.CLASSES)
            .register(registry)

        FunctionCounter.builder(
            "threads.waiting", dataSource.hikariPoolMXBean
        ) { obj -> obj.threadsAwaitingConnection.toDouble() }
            .description("The number of threads waiting for free connection")
            .baseUnit(BaseUnits.CLASSES)
            .register(registry)

        FunctionCounter.builder(
            "connections.idle", dataSource.hikariPoolMXBean
        ) { obj -> obj.idleConnections.toDouble() }
            .description("The number of idle connections")
            .baseUnit(BaseUnits.CLASSES)
            .register(registry)
    }
}
