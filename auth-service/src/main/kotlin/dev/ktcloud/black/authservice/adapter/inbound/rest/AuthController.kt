package dev.ktcloud.black.authservice.adapter.inbound.rest

import dev.ktcloud.black.auth.application.port.inbound.CreateUserCommand
import dev.ktcloud.black.auth.application.port.inbound.CreateUserUseCase
import dev.ktcloud.black.auth.application.port.inbound.ReissueTokenUseCase
import dev.ktcloud.black.auth.application.port.inbound.SignInCommand
import dev.ktcloud.black.auth.application.port.inbound.SignInUseCase
import dev.ktcloud.black.auth.domain.vo.TokenPair
import dev.ktcloud.black.auth.domain.vo.UserRole
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val signInUseCase: SignInUseCase,
    private val createUserUseCase: CreateUserUseCase,
    private val reissueTokenUseCase: ReissueTokenUseCase,
) {
    @PostMapping("/sign-in")
    fun signIn(@RequestBody request: SignInRequest): TokenPairResponse {
        val pair = signInUseCase.signIn(SignInCommand(request.email, request.password))
        return TokenPairResponse(pair.accessToken, pair.refreshToken)
    }

    @PostMapping("/sign-up")
    @ResponseStatus(HttpStatus.CREATED)
    fun signUp(@RequestBody request: SignUpRequest): SignUpResponse {
        val id = createUserUseCase.createUser(
            CreateUserCommand(
                email = request.email,
                rawPassword = request.password,
                name = request.name,
                role = request.role ?: UserRole.USER,
            )
        )
        return SignUpResponse(id)
    }

    @PostMapping("/reissue")
    fun reissue(@RequestBody request: ReissueRequest): TokenPairResponse {
        val pair = reissueTokenUseCase.reissueToken(request.refreshToken)
        return TokenPairResponse(pair.accessToken, pair.refreshToken)
    }
}

data class SignInRequest(val email: String, val password: String)
data class SignUpRequest(val email: String, val password: String, val name: String, val role: UserRole?)
data class ReissueRequest(val refreshToken: String)
data class TokenPairResponse(val accessToken: String, val refreshToken: String)
data class SignUpResponse(val userId: UUID)
