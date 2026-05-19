package dev.ktcloud.black.auth.config

import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@Configuration
@EnableJpaRepositories(basePackages = ["dev.ktcloud.black.auth.adapter.infrastructure.persistence.repository"])
class JpaConfig
