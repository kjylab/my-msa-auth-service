package dev.ktcloud.black.auth.application.port.outbound

import java.util.UUID

interface AuthCacheQueryPort {
    fun getRefreshToken(userId: UUID): String?
}
