package dev.ktcloud.black.authservice.adapter.outbound.cache

import dev.ktcloud.black.auth.application.port.outbound.AuthCacheCommandPort
import dev.ktcloud.black.auth.application.port.outbound.AuthCacheQueryPort
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import java.time.Duration
import java.util.UUID

@Component
class AuthCacheAdapter(
    private val redisTemplate: StringRedisTemplate,
) : AuthCacheQueryPort, AuthCacheCommandPort {

    override fun getRefreshToken(userId: UUID): String? =
        redisTemplate.opsForValue().get(AuthRefreshTokenRedisKey(userId).toRedisKey())

    override fun saveRefreshToken(userId: UUID, refreshToken: String, ttlSeconds: Long) {
        redisTemplate.opsForValue().set(
            AuthRefreshTokenRedisKey(userId).toRedisKey(),
            refreshToken,
            Duration.ofSeconds(ttlSeconds),
        )
    }

    override fun deleteRefreshToken(userId: UUID) {
        redisTemplate.delete(AuthRefreshTokenRedisKey(userId).toRedisKey())
    }
}
