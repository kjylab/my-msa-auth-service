package dev.ktcloud.black.auth.domain.jwt

import dev.ktcloud.black.auth.domain.exception.AuthException
import dev.ktcloud.black.auth.domain.vo.TokenClaims
import dev.ktcloud.black.auth.domain.vo.UserRole
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class JwtResolver(
    @Value("\${jwt.secret}") secret: String,
) {
    private val signingKey = Keys.hmacShaKeyFor(secret.toByteArray())

    fun resolve(token: String): TokenClaims {
        try {
            val claims = Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .payload

            return TokenClaims(
                userId = UUID.fromString(claims.subject),
                email = claims["email"] as String,
                role = UserRole.valueOf(claims["role"] as String),
            )
        } catch (e: ExpiredJwtException) {
            throw AuthException.ExpiredAccessToken()
        } catch (e: JwtException) {
            throw AuthException.InvalidToken()
        }
    }
}
