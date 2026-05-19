package dev.ktcloud.black.auth.application.port.cache.outbound

interface AuthCacheCommandOutbound {
    fun saveRefreshToken(userId: String, refreshToken: String)
    fun deleteRefreshToken(userId: String)
}
