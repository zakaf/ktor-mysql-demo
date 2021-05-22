package io.zakaf

import com.github.jasync.sql.db.mysql.MySQLConnection
import com.github.jasync.sql.db.pool.ConnectionPool
import io.micrometer.core.instrument.FunctionCounter
import io.micrometer.core.instrument.Gauge
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.binder.BaseUnits
import io.micrometer.core.instrument.binder.MeterBinder

class MySQLConnectionPoolMetrics(
    private val connectionPool: ConnectionPool<MySQLConnection>
): MeterBinder {
    override fun bindTo(registry: MeterRegistry) {
        Gauge.builder(
            "connections.in_use", connectionPool
        ) { obj -> obj.inUseConnectionsCount.toDouble() }
            .description("The number of connections in use")
            .baseUnit(BaseUnits.CLASSES)
            .register(registry)

        FunctionCounter.builder(
            "queries.waiting", connectionPool
        ) { obj -> obj.futuresWaitingForConnectionCount.toDouble() }
            .description("The number of queries waiting for free connection")
            .baseUnit(BaseUnits.CLASSES)
            .register(registry)

        FunctionCounter.builder(
            "connections.idle", connectionPool
        ) { obj -> obj.idleConnectionsCount.toDouble() }
            .description("The number of idle connections")
            .baseUnit(BaseUnits.CLASSES)
            .register(registry)
    }
}
