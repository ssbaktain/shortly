# Shortly - URL Shortener Service

> 만료/비밀번호/통계 기능을 제공하는 URL 단축 서비스

## 🛠 기술 스택

### 사용 중
- **Language**: Java 21
- **Framework**: Spring Boot 3.5.14
- **Persistence**: Spring Data JPA, Hibernate
- **Database**: MySQL 8
- **Build**: Gradle (Kotlin DSL)

### 도입 예정
- **Security**: Spring Security, JWT, Bucket4j
- **Cache**: Redis 7
- **Query**: Querydsl
- **Frontend**: React, TypeScript
- **Test**: JUnit 5, Mockito, k6
- **Infra**: Docker Compose, GitHub Actions
- **External API**: Google Safe Browsing API

## ✨ 주요 기능

### 구현 완료
- [x] URL 단축 API
- [x] 도메인 모델 (User, ShortUrl)
- [x] DB 영속성 계층

### 구현 예정
- [ ] 단축 URL 리다이렉트 API
- [ ] 회원 가입/로그인 (JWT 기반 인증)
- [ ] 만료 시간 / 비밀번호 보호 옵션
- [ ] 내 링크 관리 (목록, 수정, 삭제)
- [ ] 클릭 통계 (총 클릭 수, 일별 클릭 수)
- [ ] QR 코드 생성
- [ ] 악성 URL 차단 (Google Safe Browsing API)
- [ ] Rate Limiting

## 🎯 기술적 고민

(개발 진행하면서 채워 나갈 예정)

## 📊 성능 측정

(부하 테스트 결과를 작업하면서 추가할 예정)

## 🚀 실행 방법

(추후 작성)