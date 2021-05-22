package io.zakaf

interface FindDashboard {
    suspend fun byId(id: Long): Dashboard?
}
