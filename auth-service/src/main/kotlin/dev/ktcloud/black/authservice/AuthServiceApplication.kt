package dev.ktcloud.black.authservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["dev.ktcloud.black"])
@EntityScan(basePackages = ["dev.ktcloud.black"])
class AuthServiceApplication

fun main(args: Array<String>) {
    runApplication<AuthServiceApplication>(*args)
}
