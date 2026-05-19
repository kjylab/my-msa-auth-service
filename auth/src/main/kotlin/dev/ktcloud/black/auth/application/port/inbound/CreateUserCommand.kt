package dev.ktcloud.black.auth.application.port.inbound

import dev.ktcloud.black.auth.domain.vo.UserRole
import java.util.UUID

interface CreateUserUseCase {
    fun createUser(command: CreateUserCommand): UUID
}

data class CreateUserCommand(
    val email: String,
    val rawPassword: String,
    val name: String,
    val role: UserRole = UserRole.USER,
)
