package dev.ktcloud.black.authservice.adapter.inbound.rest

import dev.ktcloud.black.auth.domain.exception.AuthException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(AuthException.InvalidCredentials::class)
    fun handleInvalidCredentials(e: AuthException.InvalidCredentials) =
        ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ErrorResponse(e.message ?: ""))

    @ExceptionHandler(AuthException.UserAlreadyExists::class)
    fun handleUserAlreadyExists(e: AuthException.UserAlreadyExists) =
        ResponseEntity.status(HttpStatus.CONFLICT).body(ErrorResponse(e.message ?: ""))

    @ExceptionHandler(AuthException.UserNotFound::class)
    fun handleUserNotFound(e: AuthException.UserNotFound) =
        ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorResponse(e.message ?: ""))

    @ExceptionHandler(AuthException.ExpiredAccessToken::class, AuthException.InvalidToken::class)
    fun handleTokenError(e: AuthException) =
        ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ErrorResponse(e.message ?: ""))
}

data class ErrorResponse(val message: String)
