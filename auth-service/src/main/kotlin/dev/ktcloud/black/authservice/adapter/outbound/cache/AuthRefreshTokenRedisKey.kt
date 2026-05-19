package dev.ktcloud.black.authservice.adapter.outbound.cache

import java.util.UUID

data class AuthRefreshTokenRedisKey(val userId: UUID) {
    fun toRedisKey(): String = "auth-refresh-token:$userId"
    override fun toString(): String = toRedisKey()
}
