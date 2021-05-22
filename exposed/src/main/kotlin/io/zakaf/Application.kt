package io.zakaf

import com.fasterxml.jackson.databind.SerializationFeature
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
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
import org.jetbrains.exposed.sql.Database
import org.slf4j.event.Level

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module(testing: Boolean = false) {
    val appMicrometerRegistry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)

    val config = HikariConfig().apply {
        jdbcUrl = "jdbc:mysql://${environment.config.property("ktor.database.host").getString()}:${environment.config.property("ktor.database.port").getString()}/" +
                environment.config.property("ktor.database.databaseName").getString()
        driverClassName = "com.mysql.cj.jdbc.Driver"
        username = environment.config.property("ktor.database.username").getString()
        password = environment.config.property("ktor.database.password").getString()
        idleTimeout = 1000
        minimumIdle = 1
        maximumPoolSize = environment.config.property("ktor.database.connection.max").getString().toInt()
    }
    val dataSource = HikariDataSource(config)
    val db: Database = Database.connect(dataSource)

    val findDashboard = FindDashboardImpl(db)

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
            jvmMeterBinders() + hostMetrics() + LogbackMetrics() + MySQLConnectionPoolMetrics(dataSource)
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

        install(StatusPages) {
            exception<Exception> {
                call.respond(HttpStatusCode.InternalServerError)
            }
        }
    }
}
