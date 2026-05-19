package dev.ktcloud.black.authservice.adapter.inbound.rest

import dev.ktcloud.black.auth.application.port.inbound.CreateUserCommand
import dev.ktcloud.black.auth.application.port.inbound.ReissueTokenCommand
import dev.ktcloud.black.auth.application.port.inbound.SignInCommand
import dev.ktcloud.black.auth.domain.entity.JwtToken
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
    private val signInCommand: SignInCommand,
    private val createUserCommand: CreateUserCommand,
    private val reissueTokenCommand: ReissueTokenCommand,
) {
    @PostMapping("/sign-in")
    fun signIn(@RequestBody request: SignInRequest): JwtToken =
        signInCommand.signIn(SignInCommand.In(request.email, request.password)).token

    @PostMapping("/sign-up")
    @ResponseStatus(HttpStatus.CREATED)
    fun signUp(@RequestBody request: SignUpRequest): SignUpResponse {
        val id = createUserCommand.createUser(
            CreateUserCommand.In(request.email, request.password, request.name, request.role ?: UserRole.USER)
        )
        return SignUpResponse(id)
    }

    @PostMapping("/reissue")
    fun reissue(@RequestBody request: ReissueRequest): JwtToken =
        reissueTokenCommand.reissueToken(ReissueTokenCommand.In(request.refreshToken))
}

data class SignInRequest(val email: String, val password: String)
data class SignUpRequest(val email: String, val password: String, val name: String, val role: UserRole?)
data class ReissueRequest(val refreshToken: String)
data class SignUpResponse(val userId: UUID)
