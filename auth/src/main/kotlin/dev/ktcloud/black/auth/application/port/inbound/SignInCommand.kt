package dev.ktcloud.black.auth.application.port.inbound

import dev.ktcloud.black.auth.domain.vo.TokenPair

interface SignInUseCase {
    fun signIn(command: SignInCommand): TokenPair
}

data class SignInCommand(
    val email: String,
    val rawPassword: String,
)
