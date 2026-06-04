# Shortly - URL Shortener Service

> URL 단축 + JWT 인증 + 만료/비밀번호 보호 옵션이 있는 백엔드 서비스

## 🛠 기술 스택

### 사용 중
- **Language**: Java 21
- **Framework**: Spring Boot 3.5.14
- **Security**: Spring Security, JWT (jjwt), BCrypt
- **Persistence**: Spring Data JPA, Hibernate
- **Database**: MySQL 8
- **API Docs**: SpringDoc OpenAPI / Swagger UI
- **Build**: Gradle (Kotlin DSL)

### 도입 예정
- **Cache**: Redis 7
- **Query**: Querydsl
- **Frontend**: React, TypeScript
- **Test**: JUnit 5, Mockito, k6
- **Infra**: Docker Compose, GitHub Actions
- **External API**: Google Safe Browsing API
- **Rate Limiting**: Bucket4j

## ✨ 주요 기능

### 구현 완료
- [x] URL 단축 (회원/비회원 모두 가능, 비회원 URL은 `member_id` NULL)
- [x] 단축 URL 리다이렉트 (302 + Location)
- [x] 회원 가입 / 로그인 (BCrypt + JWT)
- [x] 만료 시간 옵션 (`@Future` 검증, 만료 시 410 Gone)
- [x] 비밀번호 보호 옵션 (BCrypt 해시 저장, GET 401 / POST 검증 후 302)
- [x] 내 링크 목록 조회 (JWT 필수, 페이징·정렬, secure by default)
- [x] 클릭 수 카운트 (검증 통과 시에만 증가)
- [x] 전역 예외 처리 (404 / 401 / 410 / 400 / 405 / 409 / 500)
- [x] API 문서화 (Swagger UI + Bearer 인증)

### 구현 예정
- [ ] 부하 테스트 (k6) + Redis 캐싱 전/후 RPS·p95 비교
- [ ] 클릭 통계 (일별 집계)
- [ ] 내 링크 수정 / 삭제
- [ ] QR 코드 생성
- [ ] 악성 URL 차단 (Google Safe Browsing API)
- [ ] Rate Limiting (Bucket4j — 비밀번호 brute-force 방어 포함)
- [ ] 프론트엔드 (React + TypeScript)

## 🎯 기술적 고민

(개발 진행하면서 채워 나갈 예정)

## 📊 성능 측정

(부하 테스트 결과를 작업하면서 추가할 예정)

## 🚀 실행 방법

(추후 작성)