package dev.ktcloud.black.auth.application.port.outbound

import dev.ktcloud.black.auth.domain.entity.UserEntity
import java.util.UUID

interface UserQueryPort {
    fun findByEmail(email: String): UserEntity?
    fun findById(id: UUID): UserEntity?
    fun existsByEmail(email: String): Boolean
}
