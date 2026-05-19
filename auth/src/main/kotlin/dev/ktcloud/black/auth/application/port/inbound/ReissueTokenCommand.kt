package dev.ktcloud.black.auth.application.port.inbound

import dev.ktcloud.black.auth.domain.vo.TokenPair

interface ReissueTokenUseCase {
    fun reissueToken(refreshToken: String): TokenPair
}
