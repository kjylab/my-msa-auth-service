package dev.ktcloud.black.auth.application.port.inbound

interface CheckValidityQuery {
    fun checkValidity(query: In): Out

    data class In(val accessToken: String)
    data class Out(val id: String, val email: String, val role: String, val name: String)
}
