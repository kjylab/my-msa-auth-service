package dev.ktcloud.black.auth.application.port.inbound

import dev.ktcloud.black.auth.domain.entity.JwtToken

interface SignInCommand {
    fun signIn(command: In): Out

    data class In(val email: String, val password: String)
    data class Out(val token: JwtToken)
}
