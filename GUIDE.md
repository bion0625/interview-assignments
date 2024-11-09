
# API 소개
0. ROOT 폴더에 있는 apiTest.http 파일 참조
1. 회원가입 및 로그인
    - 회원가입 등록 API : /users (POST)
    - 로그인 API : /login (POST)
2. 이용자 API
    - User 목록 불러오기 API : /users (GET)
    - User 본인 정보 불러오기 API : /users/{id} (GET)
    - User 생성 API : /users (POST)
    - User 업데이트 API : /users/{id} (PUT)
    - User 탈퇴 API : /users/{id} (DELETE)
3. 게시판 API
    - 게시판 목록 불러오기 API : /posts (GET)
    - 선택된 게시판 글 확인 API : /posts/{id} (GET)
    - 게시판 생성 API : /posts (POST)
    - 게시판 글 업데이트 API : /posts/{id} (PUT)
    - 게시판 글 삭제 API : /posts/{id} (DELETE)

# DATABASE 설치 가이드
1. ROOT 폴더 경로에서 아래 명령어 입력
```
docker-compose up
```
2. 아래 계정 정보로 접속
```
url: jdbc:postgresql://localhost:5432/postgres
id: user
password: 1234
```
3. 아래 쿼리로 테이블 생성
```
create table tb_post
(
    id         bigint generated by default as identity
        primary key,
    created_at timestamp(6) not null,
    deleted_at timestamp(6),
    updated_at timestamp(6),
    content    oid          not null,
    title      varchar(255) not null,
    user_id    bigint       not null
        constraint fkhx7a7k3pf66vpddqg5pr12anw
            references tb_user
);

create table tb_user
(
    id         bigint generated by default as identity
        primary key,
    created_at timestamp(6) not null,
    deleted_at timestamp(6),
    updated_at timestamp(6),
    age        integer,
    gender     varchar(255),
    name       varchar(255) not null,
    password   varchar(255) not null,
    phone      varchar(255),
    username   varchar(255) not null
);
```
4. 종료시 아래 명령어 입력
```
docker-compose down
```
5. ERD (ROOT 폴더의 erd.drawio.svg 파일)
![erd.drawio.svg](erd.drawio.svg)

# 테스트 시나리오
1. UserControllerTest
   - 시나리오: 가입 후 로그인 한 토큰으로 자기 자신의 정보 보기 - SUCCESS
   - 시나리오: 가입 후 로그인 한 토큰으로 타인의 정보 보기 - FAILURE
   - 시나리오: 가입 후 로그인 한 토큰으로 자기 자신의 정보 수정 - SUCCESS
   - 시나리오: 가입 후 로그인 한 토큰으로 타인의 정보 수정 - FAILURE
   - 시나리오: 가입 후 로그인 한 토큰으로 탈퇴했을 때 다시 로그인 실패 - FAILURE
2. PostServiceTest
   - 시나리오: 등록 후 등록한 본인이 수정 - SUCCESS
   - 시나리오: 타인의 게시물을 수정 - FAILURE
   - 시나리오: 등록 후 등록한 본인이 삭제 - SUCCESS
   - 시나리오: 타인의 게시물을 삭제 - FAILURE