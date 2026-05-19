package dev.ktcloud.black.auth.application.service

import dev.ktcloud.black.auth.application.port.inbound.CreateUserCommand
import dev.ktcloud.black.auth.application.port.inbound.CreateUserUseCase
import dev.ktcloud.black.auth.application.port.inbound.ReissueTokenUseCase
import dev.ktcloud.black.auth.application.port.inbound.SignInCommand
import dev.ktcloud.black.auth.application.port.inbound.SignInUseCase
import dev.ktcloud.black.auth.application.port.outbound.AuthCacheCommandPort
import dev.ktcloud.black.auth.application.port.outbound.AuthCacheQueryPort
import dev.ktcloud.black.auth.application.port.outbound.UserCommandPort
import dev.ktcloud.black.auth.application.port.outbound.UserQueryPort
import dev.ktcloud.black.auth.domain.entity.UserEntity
import dev.ktcloud.black.auth.domain.exception.AuthException
import dev.ktcloud.black.auth.domain.jwt.JwtGenerator
import dev.ktcloud.black.auth.domain.jwt.JwtResolver
import dev.ktcloud.black.auth.domain.vo.TokenPair
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class AuthCommandService(
    private val userQueryPort: UserQueryPort,
    private val userCommandPort: UserCommandPort,
    private val authCacheQueryPort: AuthCacheQueryPort,
    private val authCacheCommandPort: AuthCacheCommandPort,
    private val jwtGenerator: JwtGenerator,
    private val jwtResolver: JwtResolver,
    private val passwordEncoder: PasswordEncoder,
) : SignInUseCase, CreateUserUseCase, ReissueTokenUseCase {

    override fun signIn(command: SignInCommand): TokenPair {
        val user = userQueryPort.findByEmail(command.email)
            ?: throw AuthException.InvalidCredentials()

        if (!passwordEncoder.matches(command.rawPassword, user.password)) {
            throw AuthException.InvalidCredentials()
        }

        val tokenPair = jwtGenerator.generate(user)
        authCacheCommandPort.saveRefreshToken(
            userId = user.id,
            refreshToken = tokenPair.refreshToken,
            ttlSeconds = jwtGenerator.refreshTokenExpirySeconds(),
        )
        return tokenPair
    }

    override fun createUser(command: CreateUserCommand): UUID {
        if (userQueryPort.existsByEmail(command.email)) {
            throw AuthException.UserAlreadyExists()
        }

        val user = UserEntity(
            email = command.email,
            password = passwordEncoder.encode(command.rawPassword),
            name = command.name,
            role = command.role,
        )
        return userCommandPort.save(user).id
    }

    override fun reissueToken(refreshToken: String): TokenPair {
        val claims = jwtResolver.resolve(refreshToken)
        val stored = authCacheQueryPort.getRefreshToken(claims.userId)
            ?: throw AuthException.InvalidToken()

        if (stored != refreshToken) throw AuthException.InvalidToken()

        val user = userQueryPort.findById(claims.userId)
            ?: throw AuthException.UserNotFound()

        val newPair = jwtGenerator.generate(user)
        authCacheCommandPort.saveRefreshToken(
            userId = user.id,
            refreshToken = newPair.refreshToken,
            ttlSeconds = jwtGenerator.refreshTokenExpirySeconds(),
        )
        return newPair
    }
}
