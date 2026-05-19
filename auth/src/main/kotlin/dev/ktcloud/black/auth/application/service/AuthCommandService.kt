package dev.ktcloud.black.auth.application.service

import dev.ktcloud.black.auth.application.port.cache.outbound.AuthCacheCommandOutbound
import dev.ktcloud.black.auth.application.port.cache.outbound.AuthCacheQueryOutbound
import dev.ktcloud.black.auth.application.port.inbound.CreateUserCommand
import dev.ktcloud.black.auth.application.port.inbound.ReissueTokenCommand
import dev.ktcloud.black.auth.application.port.inbound.SignInCommand
import dev.ktcloud.black.auth.application.port.outbound.UserCommandPort
import dev.ktcloud.black.auth.application.port.outbound.UserQueryPort
import dev.ktcloud.black.auth.application.service.jwt.JwtGenerator
import dev.ktcloud.black.auth.application.service.jwt.JwtResolver
import dev.ktcloud.black.auth.domain.entity.JwtToken
import dev.ktcloud.black.auth.domain.entity.UserEntity
import dev.ktcloud.black.auth.domain.exception.AuthException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class AuthCommandService(
    private val userQueryPort: UserQueryPort,
    private val userCommandPort: UserCommandPort,
    private val authCacheQueryOutbound: AuthCacheQueryOutbound,
    private val authCacheCommandOutbound: AuthCacheCommandOutbound,
    private val jwtGenerator: JwtGenerator,
    private val jwtResolver: JwtResolver,
    private val passwordEncoder: PasswordEncoder,
) : SignInCommand, CreateUserCommand, ReissueTokenCommand {

    @Transactional
    override fun signIn(command: SignInCommand.In): SignInCommand.Out {
        val user = userQueryPort.findByEmail(command.email)
            ?: throw AuthException.InvalidCredentials()

        if (!passwordEncoder.matches(command.password, user.password))
            throw AuthException.InvalidCredentials()

        val token = jwtGenerator.generate(user)
        authCacheCommandOutbound.saveRefreshToken(user.id.toString(), token.refreshToken)

        return SignInCommand.Out(token = token)
    }

    @Transactional
    override fun createUser(command: CreateUserCommand.In): UUID {
        if (userQueryPort.existsByEmail(command.email))
            throw AuthException.UserAlreadyExists()

        val user = UserEntity(
            email = command.email,
            password = passwordEncoder.encode(command.rawPassword),
            name = command.name,
            role = command.role,
        )
        return userCommandPort.save(user).id
    }

    @Transactional
    override fun reissueToken(command: ReissueTokenCommand.In): JwtToken {
        val claims = jwtResolver.validateToken(command.refreshToken)

        val stored = authCacheQueryOutbound.getRefreshToken(claims.userId.toString())
        if (stored != command.refreshToken) {
            authCacheCommandOutbound.deleteRefreshToken(claims.userId.toString())
            throw AuthException.InvalidToken()
        }

        val user = userQueryPort.findById(claims.userId)
            ?: throw AuthException.UserNotFound()

        val newToken = jwtGenerator.generate(user)
        authCacheCommandOutbound.saveRefreshToken(user.id.toString(), newToken.refreshToken)

        return newToken
    }
}
