package dev.ktcloud.black.auth.domain.entity

import dev.ktcloud.black.auth.domain.vo.UserRole
import java.util.UUID

data class UserEntity(
    val id: UUID = UUID.randomUUID(),
    val email: String,
    val password: String,
    val name: String,
    val role: UserRole = UserRole.USER,
)
