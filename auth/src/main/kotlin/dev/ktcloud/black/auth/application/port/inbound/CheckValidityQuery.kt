package dev.ktcloud.black.auth.application.port.inbound

import dev.ktcloud.black.auth.domain.vo.TokenClaims

interface CheckValidityUseCase {
    fun checkValidity(accessToken: String): TokenClaims
}
