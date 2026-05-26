# my-msa-auth-service

Troica Market MSA의 **인증(Auth) 서비스**. 회원가입/로그인/토큰 재발급을 담당하며, JWT를 발급하고 Redis에 refresh token을 저장한다.

## 아키텍처

### 모듈 구성

```
auth/                 ← 순수 도메인 + 유스케이스 + 인프라 어댑터
  domain/             ← UserEntity, JwtToken, UserRole, AuthException
  application/
    port/inbound/     ← CreateUserCommand, SignInCommand, ReissueTokenCommand, CheckValidityQuery
    service/          ← AuthCommandService, AuthQueryService
  adapter/
    infrastructure/
      persistence/    ← PostgreSQL (users 테이블)
      redis/          ← Refresh token 저장 (AuthCacheRedisAdapter)
  config/             ← SecurityConfig, JpaConfig

auth-service/         ← 실행 진입점 (Spring Boot)
  adapter/
    inbound/
      rest/           ← AuthController (HTTP REST)
      grpc/           ← AuthGrpcAdapter (gRPC - 토큰 검증용)
  GlobalExceptionHandler
```

## REST API

| 메서드 | 경로 | 설명 | 인증 |
|--------|------|------|------|
| POST | `/api/auth/sign-up` | 회원가입 | 불필요 |
| POST | `/api/auth/sign-in` | 로그인 → JWT 발급 | 불필요 |
| POST | `/api/auth/reissue` | Access token 재발급 | Refresh token |

> user-api-gateway의 `/api/auth/**` 라우팅으로 접근

## 토큰 흐름

```
로그인 요청
  → AuthCommandService.signIn()
  → 비밀번호 검증
  → Access Token (JWT) + Refresh Token 생성
  → Refresh Token → Redis 저장 (TTL 설정)
  → 클라이언트에 Access Token + Refresh Token 반환

재발급 요청
  → Refresh Token 검증
  → Redis에서 토큰 조회
  → 새 Access Token 발급
```

## 의존 인프라

| 인프라 | 용도 |
|--------|------|
| PostgreSQL (`auth_db`, `users` 테이블) | 사용자 정보 저장 (user-service와 공유) |
| Redis | Refresh token 저장 |

## 실행 포트

| 포트 | 용도 |
|------|------|
| 8080 | HTTP REST API |

## CI/CD 흐름

```
GitHub push
  → JAR 빌드
  → Docker 이미지 빌드 + Docker Hub push (jyupk/my-msa-auth-service)
  → my-msa-manifest-values/auth-service/values-release.yaml 의 tag를 커밋 SHA로 업데이트
  → ArgoCD 감지 → 클러스터 롤링 업데이트
```

## 로컬 Docker 빌드

```bash
docker build --no-cache -t ktcloud-msa-auth-service:latest -f Containerfile .
```

## 관련 레포

| 레포 | 역할 |
|------|------|
| [my-msa-user-service](https://github.com/kjylab/my-msa-user-service) | user-service (auth_db users 테이블 공유) |
| [my-msa-user-api-gateway](https://github.com/kjylab/my-msa-user-api-gateway) | 게이트웨이 (auth 라우팅) |
| [my-msa-manifest-values](https://github.com/kjylab/my-msa-manifest-values) | Helm values |
