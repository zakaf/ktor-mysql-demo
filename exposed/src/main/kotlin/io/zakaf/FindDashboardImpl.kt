package io.zakaf

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync

class FindDashboardImpl(
    private val db: Database
) : FindDashboard {
    override suspend fun byId(id: Long): Dashboard? {
        return suspendedTransactionAsync(Dispatchers.IO, db = db) {
            DashboardEntity.findById(id)
        }.await()?.let {
            Dashboard(it.id.value, it.version, it.title)
        }
    }
}
