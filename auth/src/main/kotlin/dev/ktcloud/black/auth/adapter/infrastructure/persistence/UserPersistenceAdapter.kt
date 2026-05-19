package dev.ktcloud.black.auth.adapter.infrastructure.persistence

import dev.ktcloud.black.auth.adapter.infrastructure.persistence.entity.UserJpaEntity
import dev.ktcloud.black.auth.adapter.infrastructure.persistence.repository.UserJpaRepository
import dev.ktcloud.black.auth.application.port.outbound.UserCommandPort
import dev.ktcloud.black.auth.application.port.outbound.UserQueryPort
import dev.ktcloud.black.auth.domain.entity.UserEntity
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class UserPersistenceAdapter(
    private val userJpaRepository: UserJpaRepository,
) : UserQueryPort, UserCommandPort {

    override fun findByEmail(email: String): UserEntity? =
        userJpaRepository.findByEmail(email)?.toDomain()

    override fun findById(id: UUID): UserEntity? =
        userJpaRepository.findById(id).map { it.toDomain() }.orElse(null)

    override fun existsByEmail(email: String): Boolean =
        userJpaRepository.existsByEmail(email)

    override fun save(user: UserEntity): UserEntity =
        userJpaRepository.save(UserJpaEntity.fromDomain(user)).toDomain()
}
