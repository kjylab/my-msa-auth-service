package dev.ktcloud.black.authservice.adapter.inbound.grpc

import dev.ktcloud.black.auth.application.port.inbound.CheckValidityUseCase
import dev.ktcloud.black.auth.domain.exception.AuthException
import dev.ktcloud.black.auth.grpc.AuthServiceGrpc
import dev.ktcloud.black.auth.grpc.CheckValidityRequest
import dev.ktcloud.black.auth.grpc.CheckValidityResponse
import io.grpc.Status
import io.grpc.stub.StreamObserver
import net.devh.boot.grpc.server.service.GrpcService

@GrpcService
class AuthGrpcAdapter(
    private val checkValidityUseCase: CheckValidityUseCase,
) : AuthServiceGrpc.AuthServiceImplBase() {

    override fun checkValidity(
        request: CheckValidityRequest,
        responseObserver: StreamObserver<CheckValidityResponse>,
    ) {
        try {
            val claims = checkValidityUseCase.checkValidity(request.accessToken)
            val response = CheckValidityResponse.newBuilder()
                .setUserId(claims.userId.toString())
                .setEmail(claims.email)
                .setRole(claims.role.name)
                .build()
            responseObserver.onNext(response)
            responseObserver.onCompleted()
        } catch (e: AuthException.ExpiredAccessToken) {
            responseObserver.onError(Status.UNAUTHENTICATED.withDescription(e.message).asRuntimeException())
        } catch (e: AuthException.InvalidToken) {
            responseObserver.onError(Status.UNAUTHENTICATED.withDescription(e.message).asRuntimeException())
        } catch (e: Exception) {
            responseObserver.onError(Status.INTERNAL.withDescription(e.message).asRuntimeException())
        }
    }
}
