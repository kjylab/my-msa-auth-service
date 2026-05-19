package dev.ktcloud.black.authservice.adapter.outbound.persistence

import dev.ktcloud.black.auth.application.port.outbound.UserCommandPort
import dev.ktcloud.black.auth.application.port.outbound.UserQueryPort
import dev.ktcloud.black.auth.domain.entity.UserEntity
import dev.ktcloud.black.authservice.adapter.outbound.persistence.entity.UserJpaEntity
import dev.ktcloud.black.authservice.adapter.outbound.persistence.repository.UserJpaRepository
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
