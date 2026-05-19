package dev.ktcloud.black.auth.adapter.infrastructure.redis

import dev.ktcloud.black.auth.application.port.cache.outbound.AuthCacheQueryOutbound
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component

@Component
class AuthCacheRedisQueryAdapter(
    private val redisTemplate: RedisTemplate<String, String>,
) : AuthCacheQueryOutbound {

    override fun getRefreshToken(userId: String): String? =
        redisTemplate.opsForValue().get(AuthRefreshTokenRedisKey(userId).toRedisKey())
}
