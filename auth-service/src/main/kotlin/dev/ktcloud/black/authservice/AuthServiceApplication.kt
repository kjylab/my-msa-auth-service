package dev.ktcloud.black.authservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication(scanBasePackages = ["dev.ktcloud.black"])
@EnableJpaRepositories(basePackages = ["dev.ktcloud.black"])
@EntityScan(basePackages = ["dev.ktcloud.black"])
class AuthServiceApplication

fun main(args: Array<String>) {
    runApplication<AuthServiceApplication>(*args)
}
