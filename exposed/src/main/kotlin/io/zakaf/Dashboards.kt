package io.zakaf

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Column

object Dashboards : LongIdTable("dashboard") {
    val version: Column<Int> = integer("version")
    val title: Column<String> = varchar("title", 189)
}

class DashboardEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<DashboardEntity>(Dashboards)

    var version by Dashboards.version
    var title by Dashboards.title
}
