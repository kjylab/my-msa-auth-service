package dev.ktcloud.black.auth.application.port.inbound

import dev.ktcloud.black.auth.domain.entity.JwtToken

interface ReissueTokenCommand {
    fun reissueToken(command: In): JwtToken

    data class In(val refreshToken: String)
}
