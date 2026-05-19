package dev.ktcloud.black.auth.application.service

import dev.ktcloud.black.auth.application.port.inbound.CheckValidityUseCase
import dev.ktcloud.black.auth.domain.jwt.JwtResolver
import dev.ktcloud.black.auth.domain.vo.TokenClaims
import org.springframework.stereotype.Service

@Service
class AuthQueryService(
    private val jwtResolver: JwtResolver,
) : CheckValidityUseCase {

    override fun checkValidity(accessToken: String): TokenClaims =
        jwtResolver.resolve(accessToken)
}
