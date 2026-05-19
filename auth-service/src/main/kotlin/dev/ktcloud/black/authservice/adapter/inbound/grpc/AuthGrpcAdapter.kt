package dev.ktcloud.black.authservice.adapter.inbound.grpc

import dev.ktcloud.black.auth.application.port.inbound.CheckValidityUseCase
import dev.ktcloud.black.auth.application.port.inbound.CreateUserCommand
import dev.ktcloud.black.auth.application.port.inbound.CreateUserUseCase
import dev.ktcloud.black.auth.application.port.inbound.SignInCommand
import dev.ktcloud.black.auth.application.port.inbound.SignInUseCase
import dev.ktcloud.black.auth.domain.exception.AuthException
import dev.ktcloud.black.auth.grpc.AuthServiceGrpc
import dev.ktcloud.black.auth.grpc.CheckValidityRequest
import dev.ktcloud.black.auth.grpc.Empty
import dev.ktcloud.black.auth.grpc.SignInRequest
import dev.ktcloud.black.auth.grpc.SignInResponse
import dev.ktcloud.black.auth.grpc.SignUpRequest
import dev.ktcloud.black.auth.grpc.TokenResponseDto
import dev.ktcloud.black.auth.grpc.UserResponseDto
import io.grpc.Status
import io.grpc.stub.StreamObserver
import net.devh.boot.grpc.server.service.GrpcService

@GrpcService
class AuthGrpcAdapter(
    private val signInUseCase: SignInUseCase,
    private val createUserUseCase: CreateUserUseCase,
    private val checkValidityUseCase: CheckValidityUseCase,
) : AuthServiceGrpc.AuthServiceImplBase() {

    override fun signUp(request: SignUpRequest, responseObserver: StreamObserver<Empty>) {
        try {
            createUserUseCase.createUser(
                CreateUserCommand(
                    email = request.email,
                    rawPassword = request.plainPassword,
                    name = request.name,
                )
            )
            responseObserver.onNext(Empty.getDefaultInstance())
            responseObserver.onCompleted()
        } catch (e: AuthException.UserAlreadyExists) {
            responseObserver.onError(Status.ALREADY_EXISTS.withDescription(e.message).asRuntimeException())
        } catch (e: Exception) {
            responseObserver.onError(Status.INTERNAL.withDescription(e.message).asRuntimeException())
        }
    }

    override fun signIn(request: SignInRequest, responseObserver: StreamObserver<SignInResponse>) {
        try {
            val pair = signInUseCase.signIn(SignInCommand(request.email, request.plainPassword))
            val response = SignInResponse.newBuilder()
                .setToken(
                    TokenResponseDto.newBuilder()
                        .setAccessToken(pair.accessToken)
                        .setRefreshToken(pair.refreshToken)
                        .build()
                )
                .build()
            responseObserver.onNext(response)
            responseObserver.onCompleted()
        } catch (e: AuthException.InvalidCredentials) {
            responseObserver.onError(Status.UNAUTHENTICATED.withDescription(e.message).asRuntimeException())
        } catch (e: Exception) {
            responseObserver.onError(Status.INTERNAL.withDescription(e.message).asRuntimeException())
        }
    }

    override fun checkValidity(request: CheckValidityRequest, responseObserver: StreamObserver<UserResponseDto>) {
        try {
            val claims = checkValidityUseCase.checkValidity(request.accessToken)
            val response = UserResponseDto.newBuilder()
                .setId(claims.userId.toString())
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
