package dev.ktcloud.black.auth.domain.exception

sealed class AuthException(message: String) : RuntimeException(message) {
    class InvalidCredentials : AuthException("이메일 또는 비밀번호가 올바르지 않습니다.")
    class ExpiredAccessToken : AuthException("액세스 토큰이 만료되었습니다.")
    class InvalidToken : AuthException("유효하지 않은 토큰입니다.")
    class UserAlreadyExists : AuthException("이미 존재하는 이메일입니다.")
    class UserNotFound : AuthException("사용자를 찾을 수 없습니다.")
}
