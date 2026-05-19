package dev.ktcloud.black.authservice.adapter.outbound.persistence.repository

import dev.ktcloud.black.authservice.adapter.outbound.persistence.entity.UserJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface UserJpaRepository : JpaRepository<UserJpaEntity, UUID> {
    fun findByEmail(email: String): UserJpaEntity?
    fun existsByEmail(email: String): Boolean
}
