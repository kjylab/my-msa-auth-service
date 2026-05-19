package dev.ktcloud.black.auth.application.port.outbound

import java.util.UUID

interface AuthCacheCommandPort {
    fun saveRefreshToken(userId: UUID, refreshToken: String, ttlSeconds: Long)
    fun deleteRefreshToken(userId: UUID)
}
