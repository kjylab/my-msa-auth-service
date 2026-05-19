package dev.ktcloud.black.auth.domain.jwt

import dev.ktcloud.black.auth.domain.entity.UserEntity
import dev.ktcloud.black.auth.domain.vo.TokenPair
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.Date

@Component
class JwtGenerator(
    @Value("\${jwt.secret}") secret: String,
    @Value("\${jwt.access-token-expiry-seconds:1800}") private val accessTokenExpirySeconds: Long,
    @Value("\${jwt.refresh-token-expiry-seconds:604800}") private val refreshTokenExpirySeconds: Long,
) {
    private val signingKey = Keys.hmacShaKeyFor(secret.toByteArray())

    fun generate(user: UserEntity): TokenPair {
        val now = System.currentTimeMillis()
        return TokenPair(
            accessToken = buildToken(user, now, accessTokenExpirySeconds * 1000),
            refreshToken = buildToken(user, now, refreshTokenExpirySeconds * 1000),
        )
    }

    private fun buildToken(user: UserEntity, nowMillis: Long, expiryMillis: Long): String =
        Jwts.builder()
            .subject(user.id.toString())
            .claim("email", user.email)
            .claim("role", user.role.name)
            .issuedAt(Date(nowMillis))
            .expiration(Date(nowMillis + expiryMillis))
            .signWith(signingKey)
            .compact()

    fun refreshTokenExpirySeconds(): Long = refreshTokenExpirySeconds
}
