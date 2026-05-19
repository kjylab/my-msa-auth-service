object Versions {
    const val GRPC = "4.34.1"
    const val GRPC_KOTLIN = "1.4.1"
    const val GRPC_PROTO = "1.80.0"
}

plugins {
    id("org.springframework.boot")
    kotlin("plugin.jpa")
    id("com.google.protobuf")
}

dependencies {
    implementation(project(":auth"))

    // Web
    implementation("org.springframework.boot:spring-boot-starter-web")

    // gRPC
    implementation("net.devh:grpc-server-spring-boot-starter:3.1.0.RELEASE")
    implementation("com.google.protobuf:protobuf-kotlin:${Versions.GRPC}")
    implementation("io.grpc:grpc-kotlin-stub:${Versions.GRPC_KOTLIN}")
    implementation("io.grpc:grpc-protobuf:${Versions.GRPC_PROTO}")
    implementation("io.grpc:grpc-stub:${Versions.GRPC_PROTO}")
    implementation("io.grpc:grpc-netty-shaded:${Versions.GRPC_PROTO}")

    // JPA + PostgreSQL + Flyway
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("org.postgresql:postgresql")
    implementation("org.flywaydb:flyway-database-postgresql")

    // Redis
    implementation("org.springframework.boot:spring-boot-starter-data-redis")

    // Actuator + Prometheus
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    runtimeOnly("io.micrometer:micrometer-registry-prometheus")

    // Tracing
    implementation("io.micrometer:micrometer-tracing-bridge-otel")
    implementation("io.opentelemetry:opentelemetry-exporter-otlp")
}

tasks.bootJar {
    archiveFileName.set("auth-service.jar")
}

sourceSets {
    getByName("main") {
        java {
            srcDirs(
                "build/generated/source/proto/main/java",
                "build/generated/source/proto/main/kotlin",
            )
        }
    }
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:${Versions.GRPC}"
    }
    plugins {
        register("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:${Versions.GRPC_PROTO}"
        }
        register("grpckt") {
            artifact = "io.grpc:protoc-gen-grpc-kotlin:${Versions.GRPC_KOTLIN}:jdk8@jar"
        }
    }
    generateProtoTasks {
        all().forEach {
            it.plugins {
                register("grpc")
                register("grpckt")
            }
            it.builtins {
                register("kotlin")
            }
        }
    }
}
