package dev.ktcloud.black.auth.application.service.jwt

import dev.ktcloud.black.auth.domain.entity.JwtToken
import dev.ktcloud.black.auth.domain.entity.UserEntity
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.Date

@Component
class JwtGenerator(
    @Value("\${jwt.secret}") private val secretKey: String,
) {
    companion object {
        const val ACCESS_TOKEN_DURABILITY = 1000 * 60 * 30L
        const val REFRESH_TOKEN_DURABILITY = 1000 * 60 * 60 * 24 * 7L
    }

    fun generate(user: UserEntity): JwtToken {
        val key = Keys.hmacShaKeyFor(secretKey.toByteArray())
        val now = Date()

        val accessToken = Jwts.builder()
            .subject(user.id.toString())
            .claims(
                mapOf(
                    "email" to user.email,
                    "role" to user.role.name,
                    "name" to user.name,
                )
            )
            .issuedAt(now)
            .expiration(Date(now.time + ACCESS_TOKEN_DURABILITY))
            .signWith(key)
            .compact()

        val refreshToken = Jwts.builder()
            .subject(user.id.toString())
            .issuedAt(now)
            .expiration(Date(now.time + REFRESH_TOKEN_DURABILITY))
            .signWith(key)
            .compact()

        return JwtToken(accessToken, refreshToken)
    }
}
