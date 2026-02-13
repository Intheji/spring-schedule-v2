# 📚 일정 관리 앱 (Spring Schedule)

  Spring Boot + Spring Data JPA + MySQL로 **유저/일정/댓글 CRUD**를 구현한 REST API입니다.  
  세션 기반 로그인(인증), 권한 체크(인가), 입력값 검증(Validation), 전역 예외 처리, 페이지네이션, Soft Delete 적용

  ---

  ## ✅ 주요 기능

  ### 👤 유저(User)
  - 회원가입 / 유저 조회(전체/단건) / 유저 수정 / 유저 삭제

  ### 🔐 인증(Auth)
  - 로그인(세션 생성) / 로그아웃(세션 만료) / 로그인 확인 테스트

  ### 🗓️ 일정(Schedule)
  - 일정 생성 / 일정 단건 조회 / 일정 목록 조회(페이징 + 작성자 필터) / 일정 수정 / 일정 삭제(Soft Delete)

  ### 💬 댓글(Comment)
  - 댓글 생성 / 특정 일정의 댓글 목록 조회 / 댓글 수정 / 댓글 삭제

  ---

  ## 🧰 기술 스택
  - Java 17
  - Spring Boot
  - Spring WebMVC
  - Spring Data JPA
  - MySQL
  - Bean Validation
  - BCrypt(비밀번호 해싱)
  - Lombok

  ---

  ## 🗂️ 패키지 구조
  ```yml
  com.springschedule
    - auth
      - controller
      - dto
    - user
      - controller
      - dto
      - entity
      - repository
      - service
    - schedule
      - controller
      - dto
      - entity
      - repository
      - service
    - comment
      - controller
      - dto
      - entity
      - repository
      - service
    - common
      - entity     # BaseEntity (createdAt/modifiedAt)
      - exception  # ErrorResponse / GlobalExceptionHandler
    - config
      - JpaConfig        # JPA Auditing
      - PasswordEncoder  # BCrypt
  ```

  ---

  ## 🧾 ERD
  ```mermaid
  erDiagram
    USERS ||--o{ SCHEDULES : writes
    USERS ||--o{ COMMENTS : writes
    SCHEDULES ||--o{ COMMENTS : has

    USERS {
      BIGINT id PK
      VARCHAR user_name
      VARCHAR email
      VARCHAR password
      DATETIME created_at
      DATETIME modified_at
    }

    SCHEDULES {
      BIGINT id PK
      VARCHAR title
      VARCHAR content
      BIGINT user_id FK
      DATETIME deleted_at
      DATETIME created_at
      DATETIME modified_at
    }

    COMMENTS {
      BIGINT id PK
      VARCHAR content
      BIGINT schedule_id FK
      BIGINT user_id FK
      DATETIME created_at
      DATETIME modified_at
    }
  ```

  ---

  ## 🔐 인증/인가 정책(세션)
  - 로그인 성공 시 `HttpSession`에 `loginUserId` 저장
  - 아래 API는 로그인 필요(세션 없으면 401)
    - 일정 생성/수정/삭제
    - 댓글 생성/수정/삭제
  - 작성자만 수정/삭제 가능(작성자 불일치 시 400 처리)

  ---

  ## 🧹 Soft Delete (일정)
  - 일정 삭제 시 DB에서 row를 제거하지 않고 `deleted_at`에 삭제 시각을 저장합니다.
  - 조회 API는 `deleted_at is null`인 데이터만 조회합니다.


  ```

  ---

  ## 📄 공통 에러 응답 형식
  전역 예외 처리(`@RestControllerAdvice`)로 에러 응답을 통일했습니다. 밑은 예시

  ```json
  {
    "timestamp": "0000-00-00T00:00:00",
    "status": 400,
    "error": "BAD_REQUEST",
    "code": "VALIDATION_FAILED",
    "message": "page는 1 이상이어야 합니다.",
    "path": "/schedules"
  }
  ```

  - 400: 입력값 검증 실패 / 요청값 오류
  - 404: 리소스 없음(일정/유저/댓글 없음)

  ---

  ## 📌 API 명세 요약

  | 구분 | Method | URL | 인증 | 설명 |
  |---|---|---|---|---|
  | 유저 | POST | /users | X | 회원가입 |
  | 유저 | GET | /users | X | 유저 목록 조회 |
  | 유저 | GET | /users/{userId} | X | 유저 단건 조회 |
  | 유저 | PATCH | /users/{userId} | X | 유저 수정 |
  | 유저 | DELETE | /users/{userId} | X | 유저 삭제 |
  | 인증 | POST | /auth/login | X | 로그인(세션 생성) |
  | 인증 | POST | /auth/logout | X | 로그아웃(세션 만료) |
  | 인증 | GET | /auth/test | O | 로그인 여부 테스트 |
  | 일정 | POST | /schedules | O | 일정 생성 |
  | 일정 | GET | /schedules/{scheduleId} | X | 일정 단건 조회 |
  | 일정 | GET | /schedules?page=1&size=10&userName=... | X | 일정 페이징 조회(+작성자 필터) |
  | 일정 | PATCH | /schedules/{scheduleId} | O | 일정 수정(작성자만) |
  | 일정 | DELETE | /schedules/{scheduleId} | O | 일정 삭제(Soft Delete, 작성자만) |
  | 댓글 | POST | /schedules/{scheduleId}/comments | O | 댓글 생성 |
  | 댓글 | GET | /schedules/{scheduleId}/comments | X | 댓글 목록 조회 |
  | 댓글 | PATCH | /schedules/{scheduleId}/comments/{commentId} | O | 댓글 수정(작성자만) |
  | 댓글 | DELETE | /schedules/{scheduleId}/comments/{commentId} | O | 댓글 삭제(작성자만) |

  ---

  ## 🧩 API 상세

  ### 1) 로그인
  `POST /auth/login`

  **Request**
  ```json
  {
    "email": "hyunji@gmail.com",
    "password": "qlalfqjsgh"
  }
  ```

  **Response**
  - 200 OK (Body 없음)
  - 로그인 성공 시 세션에 `loginUserId` 저장

  ---

  ### 2) 일정 페이징 조회
  `GET /schedules?page=1&size=10&userName=박현지`

  **Query Params**
  - `page` (default 1, min 1)
  - `size` (default 10, min 1, max 30)
  - `userName` (optional)

  **Response(요약)**
  ```json
  {
    "content": [
      {
        "id": 25,
        "title": "테스트",
        "content": "내용",
        "userName": "박현지",
        "commentCount": 2,
        "createdAt": "2026-02-12T12:39:55.093801",
        "modifiedAt": "2026-02-12T12:39:55.093801"
      }
    ],
    "totalPages": 2,
    "totalElements": 25,
    "size": 10,
    "number": 0,
    "first": true,
    "last": false
  }
  ```

  ---

  ## ▶️ 실행 방법

  - IntelliJ에서 `SpringScheduleV2Application` 실행

  ---
  블로그 https://ggoongdeng.tistory.com/244
  포스트맨 https://documenter.getpostman.com/view/51137755/2sBXcBmhCy
