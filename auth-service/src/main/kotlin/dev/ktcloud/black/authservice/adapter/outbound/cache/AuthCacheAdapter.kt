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

    private fun key(userId: UUID) = "refresh:$userId"

    override fun getRefreshToken(userId: UUID): String? =
        redisTemplate.opsForValue().get(key(userId))

    override fun saveRefreshToken(userId: UUID, refreshToken: String, ttlSeconds: Long) {
        redisTemplate.opsForValue().set(key(userId), refreshToken, Duration.ofSeconds(ttlSeconds))
    }

    override fun deleteRefreshToken(userId: UUID) {
        redisTemplate.delete(key(userId))
    }
}
