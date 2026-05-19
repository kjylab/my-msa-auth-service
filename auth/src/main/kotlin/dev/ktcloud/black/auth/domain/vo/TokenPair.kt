package dev.ktcloud.black.auth.domain.vo

data class TokenPair(
    val accessToken: String,
    val refreshToken: String,
)
