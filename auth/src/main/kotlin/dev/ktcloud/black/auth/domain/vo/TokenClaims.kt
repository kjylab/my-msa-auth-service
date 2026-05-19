package dev.ktcloud.black.auth.domain.vo

import java.util.UUID

data class TokenClaims(
    val userId: UUID,
    val email: String,
    val role: UserRole,
)
