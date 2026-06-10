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
- **Load Test**: k6
- **Rate Limiting**: Bucket4j
- **Build**: Gradle (Kotlin DSL)

### 도입 예정
- **Cache**: Redis 7
- **Query**: Querydsl
- **Frontend**: React, TypeScript
- **Unit Test**: JUnit 5, Mockito
- **Infra**: Docker Compose, GitHub Actions
- **External API**: Google Safe Browsing API

## ✨ 주요 기능

### 구현 완료
- [x] URL 단축 (회원/비회원 모두 가능, 비회원 URL은 `member_id` NULL)
- [x] 단축 URL 리다이렉트 (302 + Location)
- [x] 회원 가입 / 로그인 (BCrypt + JWT)
- [x] 만료 시간 옵션 (`@Future` 검증, 만료 시 410 Gone)
- [x] 비밀번호 보호 옵션 (BCrypt 해시 저장, GET 401 / POST 검증 후 302)
- [x] 내 링크 목록 조회 (JWT 필수, 페이징·정렬, secure by default)
- [x] 클릭 수 카운트 (검증 통과 시에만 증가)
- [x] Rate Limiting (Bucket4j) — POST 비번 보호 URL brute-force 차단 (Token Bucket, capacity 5 + 분당 3 회 refill)
- [x] 전역 예외 처리 (404 / 401 / 410 / 400 / 405 / 409 / 429 / 500)
- [x] API 문서화 (Swagger UI + Bearer 인증)
- [x] 부하 테스트 (k6) — 동시성 결함 측정·수정 (POST 실패율 74%→0%, GET 클릭 손실율 90%→0%) + brute-force 차단율 99.999%

### 구현 예정
- [ ] Redis 캐싱 도입 + 부하 테스트 RPS·p95 비교
- [ ] 클릭 통계 (일별 집계)
- [ ] 내 링크 수정 / 삭제
- [ ] QR 코드 생성
- [ ] 악성 URL 차단 (Google Safe Browsing API)
- [ ] 프론트엔드 (React + TypeScript)

## 🎯 기술적 고민

(개발 진행하면서 채워 나갈 예정)

## 📊 성능 측정

k6 로 부하 — POST `/api/urls` (단축 생성)과 GET `/{shortKey}` (리다이렉트) 두 시나리오.

### 측정 결과 네 개

**1. collation case-insensitive 로 키 공간 압축**

1 VU 에서 실패율 60%. 동시성이 아니라 `short_key` 가 MySQL 기본 `utf8mb4_unicode_ci` 로 만들어져
Base62 키를 case-insensitive 비교 → 키 공간 62ⁿ → 36ⁿ 로 압축. 1295 건부터 충돌.
`utf8mb4_bin` 으로 변경.

**2. INSERT-then-UPDATE 패턴이 lock 회로**

100 VU 에서 실패율 74%, InnoDB deadlock. 단축 키 생성이 `"temp"` 임시 키로 INSERT 후
ID 받아 진짜 키로 UPDATE 하는 구조라, 동시 INSERT 가 모두 같은 unique 슬롯에 lock 잡혀 회로 형성.
`SecureRandom` 7 자 키를 미리 생성해 INSERT 한 번으로 끝내는 구조로 변경.
7 자 = 62⁷ ≈ 3.5조 공간 — 1억 row 까지 충돌 무시 가능, 그 이상은 컬럼이 `VARCHAR(10)` 이라
8~9 자로 확장 가능.

**3. read-modify-write race 로 click_count 손실**

100 VU GET `/{shortKey}` 부하에서 클릭 손실율 90%. `getOriginalUrl()` 가 SELECT → 메모리 ++ → UPDATE
3 단계로 카운트를 증가시키는 구조라 동시 트랜잭션이 같은 시작값을 읽으면 last writer wins 으로 손실.
JPQL `UPDATE SET click_count = click_count + 1` atomic statement 로 변경 — read+modify+write 를
DB 단일 statement 에 묶어 row lock 으로 직렬화.

**4. 비밀번호 보호 URL brute-force 방어 (Bucket4j Token Bucket)**

POST `/{shortKey}` 에 IP + shortKey 키 단위 Bucket4j 적용 (capacity 5, refill 분당 3 회).
100 VU 30s brute-force 부하 → 차단율 99.999% (785,935 / 785,942), 시간당 실효 시도 약 185 회
→ 4 자리 숫자 비번 전수 54 시간 / 5 자리 22.5 일 → 사실상 비효율화. rate limit 거절은 Interceptor
단계 컷이라 컨트롤러·service 진입 X — p95 7ms / RPS 26k.

### 결과

**POST `/api/urls` — 0→100 VU 100s ramp**

| 지표 | Before | After |
|---|---|---|
| 실패율 | 74.4% | 0.00% |
| 성공 요청 수 | 21,539 | 127,159 |
| RPS | 841 | 1,271 |
| p95 | 103ms | 73ms |
| 최대 | 1.32s | 189ms |

**GET `/{shortKey}` — 100 VU 30s**

| 지표 | Before | After |
|---|---|---|
| 손실율 | 89.96% | 0.00% |
| 클릭 카운트 (DB) | 1,773 | 21,242 |
| 성공 요청 수 | 17,655 | 21,242 |
| RPS | 588 | 708 |
| p95 | 471ms | 394ms |

**POST `/{shortKey}` brute-force — 100 VU 30s (rate limit 적용 후)**

| 지표 | 값 |
|---|---|
| 차단율 | 99.999% (785,935 / 785,942) |
| 401 통과 (capacity + refill) | 6 |
| RPS | 26,093 |
| p95 | 7.01ms |

## 🚀 실행 방법

(추후 작성)