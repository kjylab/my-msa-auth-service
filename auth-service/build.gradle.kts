plugins {
    id("org.springframework.boot")
    kotlin("plugin.jpa")
}

dependencies {
    implementation(project(":auth"))

    // Web
    implementation("org.springframework.boot:spring-boot-starter-web")

    // gRPC
    implementation("net.devh:grpc-server-spring-boot-starter:3.1.0.RELEASE")

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

    // Proto
    implementation("com.google.protobuf:protobuf-java:3.25.3")
    implementation("io.grpc:grpc-stub:1.62.2")
    implementation("io.grpc:grpc-protobuf:1.62.2")
    implementation("javax.annotation:javax.annotation-api:1.3.2")
}

tasks.bootJar {
    archiveFileName.set("auth-service.jar")
}

sourceSets {
    getByName("main") {
        java {
            srcDirs(
                "build/generated/source/proto/main/java",
                "build/generated/source/proto/main/grpc",
            )
        }
    }
}

protobuf {
    protoc { artifact = "com.google.protobuf:protoc:3.25.3" }
    plugins {
        id("grpc") { artifact = "io.grpc:protoc-gen-grpc-java:1.62.2" }
    }
    generateProtoTasks {
        all().forEach {
            it.plugins { id("grpc") }
        }
    }
}
