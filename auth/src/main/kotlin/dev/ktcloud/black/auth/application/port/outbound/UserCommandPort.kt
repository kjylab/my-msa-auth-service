package dev.ktcloud.black.auth.application.port.outbound

import dev.ktcloud.black.auth.domain.entity.UserEntity

interface UserCommandPort {
    fun save(user: UserEntity): UserEntity
}
