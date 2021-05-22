package io.zakaf

import com.fasterxml.jackson.databind.SerializationFeature
import com.github.jasync.sql.db.ConnectionPoolConfigurationBuilder
import com.github.jasync.sql.db.mysql.MySQLConnection
import com.github.jasync.sql.db.mysql.MySQLConnectionBuilder
import com.github.jasync.sql.db.pool.ConnectionPool
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.*
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.jackson
import io.ktor.metrics.micrometer.MicrometerMetrics
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.routing
import io.micrometer.core.instrument.binder.logging.LogbackMetrics
import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry
import org.slf4j.event.Level

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module(testing: Boolean = false) {
    val appMicrometerRegistry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)

    val connectionPool: ConnectionPool<MySQLConnection> = ConnectionPoolConfigurationBuilder().apply {
        host = environment.config.property("ktor.database.host").getString()
        port = environment.config.property("ktor.database.port").getString().toInt()
        database = environment.config.property("ktor.database.databaseName").getString()
        username = environment.config.property("ktor.database.username").getString()
        password = environment.config.property("ktor.database.password").getString()
        maxActiveConnections = environment.config.property("ktor.database.connection.max").getString().toInt()
        maxIdleTime = 1000
        applicationName = "ktor-jasync-demo"
    }.let {
        MySQLConnectionBuilder.createConnectionPool(it)
    }.also {
        it.connect()
    }

    val findDashboard = FindDashboardImpl(connectionPool)

    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }

    install(CallLogging) {
        level = Level.INFO
        format { call -> "${call.request.toLogString()} ${call.request.queryParameters.entries()}" }
    }

    install(MicrometerMetrics) {
        registry = appMicrometerRegistry
        distributionStatisticConfig = distributionStatisticsConfig()
        timers { _, _ ->
            tag("region", "")
        }
        meterBinders =
            jvmMeterBinders() + hostMetrics() + LogbackMetrics() + MySQLConnectionPoolMetrics(connectionPool)
    }

    routing {
        get("/dashboards/{id}") {
            call.parameters["id"]?.toLong()?.let {
                findDashboard.byId(it)
            }?.let {
                call.respond(HttpStatusCode.OK, it)
            } ?: throw NotFoundException()
        }

        get("/_internal/prometheus") {
            call.respond(appMicrometerRegistry.scrape())
        }

        get("/hello") {
            call.respond(HttpStatusCode.OK, "HELLO WORLD!")
        }

        install(StatusPages) {
            exception<Exception> {
                call.respond(HttpStatusCode.InternalServerError)
            }
        }
    }
}
