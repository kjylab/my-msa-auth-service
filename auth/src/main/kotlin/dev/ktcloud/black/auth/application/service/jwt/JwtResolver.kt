package dev.ktcloud.black.auth.application.service.jwt

import dev.ktcloud.black.auth.domain.exception.AuthException
import dev.ktcloud.black.auth.domain.vo.UserRole
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class JwtResolver(
    @Value("\${jwt.secret}") private val secretKey: String,
) {
    private val signingKey by lazy { Keys.hmacShaKeyFor(secretKey.toByteArray()) }

    fun extractClaims(token: String): Claims {
        return try {
            Jwts.parser().verifyWith(signingKey).build()
                .parseSignedClaims(token).payload
        } catch (e: ExpiredJwtException) {
            throw AuthException.ExpiredAccessToken()
        } catch (e: JwtException) {
            throw AuthException.InvalidToken()
        }
    }

    fun validateToken(token: String): TokenClaims {
        val claims = extractClaims(token)
        return TokenClaims(
            userId = UUID.fromString(claims.subject),
            email = claims["email"] as String,
            role = UserRole.valueOf(claims["role"] as String),
            name = claims["name"] as? String ?: "",
        )
    }
}

data class TokenClaims(
    val userId: UUID,
    val email: String,
    val role: UserRole,
    val name: String,
)
