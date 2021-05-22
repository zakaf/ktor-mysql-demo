package io.zakaf

import com.github.jasync.sql.db.asSuspending
import com.github.jasync.sql.db.mysql.MySQLConnection
import com.github.jasync.sql.db.pool.ConnectionPool

class FindDashboardImpl(
    private val connectionPool: ConnectionPool<MySQLConnection>
) : FindDashboard, AutoCloseable {
    override fun close() {
        connectionPool.disconnect()
    }

    override suspend fun byId(id: Long): Dashboard? {
        return connectionPool.asSuspending.sendPreparedStatement(
            "select id, version, title from dashboard where id = ?",
            listOf(id)
        ).rows.firstOrNull()?.let {
            Dashboard(it.getLong(0)!!, it.getInt(1)!!, it.getString(2)!!)
        }
    }
}
