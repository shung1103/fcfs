
# 🛍️ First Come First Serve

<br>

## 👉🏻 서비스 소개

    집에서도 햄버거의 맛을 놓치지 마세요! 햄버거 포장&메뉴 웹사이트로 원하는 햄버거를 주문하고, 배달 받고, 즐기세요! 
    BD는 당신의 입맛에 맞는 햄버거를 쉽고 빠르게 제공합니다. 
    다양한 종류의 햄버거, 사이드 메뉴, 음료수 등을 원하는 조합으로 선택하세요. 
    햄버거 포장&메뉴 웹사이트와 함께 라면 언제 어디 서나 맛있는 햄버거를 즐길 수 있습니다.

<br>

## ❓기획 의도

    MSA를 통한 서비스 간 결합도 및 의존도 경감
    특정 시간의 선착순 구매로 집중된 대규모 트래픽 처리

<br>

## 개발 환경

     Spring Boot Version : 3.3.4
     Spring Cloud Version : 2023.0.3
     JDK Version : 21
     Java Language Version : 21

<br>

## 🗓️ 프로젝트 기간

    2024년 10월 15일 ~ 2024년 11월 12일

<br>

## 🔧 사용한 Tool

<div style="display: flex; justify-content: center;">
  <img src="https://img.shields.io/badge/Java-007396?&style=flat&logo=Java&logoColor=white" style="margin-right: 10px;">
  <img src="https://img.shields.io/badge/Spring-6DB33F?&style=flat&logo=spring&logoColor=white" style="margin-right: 10px;">
  <img src="https://img.shields.io/badge/Spring Security-6DB33F?&style=flat&logo=spring security&logoColor=white" style="margin-right: 10px;">
  <img src="https://img.shields.io/badge/MSA-D9232E?&style=flat&logo=MSA&logoColor=white" style="margin-right: 10px;">
  <img src="https://img.shields.io/badge/Amazon API Gateway-FF4F8B?&style=flat&logo=GateWay&logoColor=white" style="margin-right: 10px;">
</div>

<div style="display: flex; justify-content: center;">
  <img src="https://img.shields.io/badge/Intellijidea-000000?style=flat&logo=intellijidea&logoColor=white" style="margin-right: 10px;">
  <img src="https://img.shields.io/badge/Git-F05032?style=flat&logo=git&logoColor=white" style="margin-right: 10px;">
  <img src="https://img.shields.io/badge/Github-181717?style=flat&logo=github&logoColor=white" style="margin-right: 10px;">
  <img src="https://img.shields.io/badge/Postman-FF6C37?style=flat&logo=postman&logoColor=white" style="margin-right: 10px;">
  <img src="https://img.shields.io/badge/Swagger-85EA2D?style=flat&logo=Swagger&logoColor=white" style="margin-right: 10px;"/>
</div>

<div style="display: flex; justify-content: center;">
  <img src="https://img.shields.io/badge/Redis-DC382D?style=flat&logo=Redis&logoColor=white" style="margin-right: 10px;">
  <img src="https://img.shields.io/badge/MySQL-4479A1?style=flat&logo=mysql&logoColor=white" style="margin-right: 10px;"/>
  <img src="https://img.shields.io/badge/Docker-2496ED?style=flat&logo=Docker&logoColor=white" style="margin-right: 10px;"/>
  <img src="https://img.shields.io/badge/Bitbucket-0052CC?style=flat&logo=bucket4j&logoColor=white" style="margin-right: 10px;">
  <img src="https://img.shields.io/badge/Awselasticloadbalancing-8C4FFF?style=flat&logo=Load Balancer&logoColor=white" style="margin-right: 10px;"/>
</div>

<br>
<h2>⚙️서비스 아키텍처</h2>
<p align="center">
  <img src="https://github.com/burger-drop/burger-drop-repo/assets/94231335/6d9e5d0d-3196-4ba6-8464-83a8caf078fa" style="margin-right: 10px;">
</p>


<br>

# ✨주요 기능
1. Redis를 활용한 Refresh token 운용 - JWT의 취약점 보완 및 로그인 상태 유지
2. AWS S3를 활용한 이미지 파일 업로드 및 저장
3. SMTP를 활용한 이메일 인증 기능
4. WebSocket, stomp, Redis 사용한 채팅 기능
5. AWS EC2를 사용한 배포
6. Redis와 Interceptor를 활용한 방문자 정보 저장 - 총 방문자 수와 주된 방문 경로 파악
7. Swagger 연동 - API 명세서 작성 시간 단축 및 API 테스트 보조
8. 위치기반 API를 활용한 거리계산, 배달예정시간 계산
9. Redis Cache 적용 - 상품 목록 조회, 상품 단건 조회
10. Docker 사용 - 최대한 환경에 구애받지 않고 안정적으로 프로그램 구동 및 배포, 확장 가능
11. 상품 재입고 시 이메일 알림기능

<br>

## 💫 기능

`회원 가입 / 로그인`
- 유저는 회원가입 시 이메일 인증을 해야한다.
- 소셜 로그인 가능 (구글, 카카오, 네이버)
    - 소셜 로그인 버튼 클릭 → 기존 회원이면 로그인 → 기존 회원 아니면 회원가입

`유저`
- 유저 페이지에서 정보 조회, 수정, 탈퇴, 로그아웃 가능하다.
- 주문내역과 리뷰 작성 내역을 볼 수 있다.

`상품`
- 회원 가입이나 로그인 하지 않아도 전체 상품 조회가 가능하다.
- 회원 가입이나 로그인 하지 않아도 상품 키워드 검색 기능을 사용할 수 있다.
- 관리자는 상품 생성, 조회, 수정, 삭제가 가능하다.
- 관리자는 옵션 생성, 조회, 수정, 삭제가 가능하다.
- 카테고리 별로 상품을 확인할 수 있다. [햄버거, 음료, 사이드]

`배달 / 포장`
- 포장을 선택한다면 조리 시간을 더한 시간이 포장 시간으로 표시된다.
- 배달을 선택한다면 원래 금액에서 2000원이 추가된다.
- 주문 완료 시간에 배달 시간을 추가하여 총 시간이 표시가 되는데 여기서 배달 시간이란 매장과 사용자 간의 거리를 위도와 경도를 통해서 1km 당 1분 씩 추가한 시간을 말한다.
- 사용자는 자신의 모든 주문 목록 중 최신의 10개까지 조회할 수 있다.
- 사용자는 자신의 준비 중인 주문을 취소할 수 있다.
- 관리자는 최대 100개의 최신의 완료된 주문 목록과 모든 준비 중인 주문 목록을 조회할 수 있다.
- 5분마다 전체 준비 중인 주문 목록을 조회하여 완료 시간을 넘은 주문을 완료 상태로 바꾼다.

`알림`
- 사이트를 이용 중인 유저에게 SSE(Server Sent Event)를 이용하여 실시간 알람을 기능 제공하고 있다.
- 회원정보 수정, 주문 완료 및 취소, 배달 완료시 알림이 표시된다.
- 알림 전체 삭제가 가능하다.

<br>

# ✔️ 기술적 의사 결정

<details>
<summary>검색 기능에 QueryDsl 적용 여부</summary>
<div markdown="1">       

<br>

### - 기술의 개념

    - QueryDSL은 하이버네이트 쿼리 언어(HQL: Hibernate Query Language)의 쿼리를 타입에 안전하게 생성 및 관리해주는 프레임워크이다. 정적 타입을 이용하여 SQL과 같은 쿼리를 생성할 수 있게 해 준다. 자바 백엔드 기술은 Spring Boot와 Spring Data JPA를 함께 사용한다. 하지만, 복잡한 쿼리, 동적 쿼리를 구현하는 데 있어 한계가 있다. 이러한 문제점을 해결할 수 있는 것이 QueryDSL이다.

     - 왜 이 기술을 선택했는지?

    - 검색할 때 사용자가 바라는 특징에 부합하는 상품을 검색 결과로 보여주어 더 나은 서비스를 제공하기 위해서이다.
    - JPQL과 비교하였을 때 가독성이 높고 확장 가능한 동적 쿼리를 작성할 수 있어 Querydsl을 사용했다.

     - 기술의 장, 단점

    - 장점
        - 사용자가 원하는 상품을 조금 더 빠르게 찾을 수 있다.
    - 단점
        - 메인 페이지의 로딩 시간이 증가한다.
        - 이미 카테고리 별로 나누어서 조회하는 기능이 있기에 큰 쓸모가 없다.
        - 검색 기능을 위해 상품 엔티티의 컬럼이 쓸데없이 증가할 수 있다.

</div>
</details>

<br>

# 🙃 트러블 슈팅

<details>
<summary>JWT의 보안적 취약성</summary>
<div markdown="1">       

- 만료 기한이 짧은 엑세스 토큰과 만료 기한이 긴 리프레시 토큰을 사용
- 리프레시 토큰은 영구적으로 보관할 필요가 없으니 속도가 빠른 레디스를 저장소로 사용
- 리프레시 토큰까지 탈취 되었을 때를 대비하여 강제종료 API 마련

</div>
</details>

<details>
<summary>Redis 키 조회 시 병목현상 발생</summary>
<div markdown="1">       

- Redis는 단일 쓰레드 아키텍쳐이기에 처리가 오래 걸리는 명령을 요청할 경우 그 동작이 마무리 될 때가지 다른 요청을 멈춰두게 되어 병목현상이 발생하게 된다. 특히나 keys, flushall 등의 명령어는 테스트나 소량의 데이터 환경에서는 괜찮지만 점차 데이터를 쌓아가는 환경에서는 운영에 차질을 빚을 정도로 속도가 느려지는 문제가 있다.
- keys 명령어를 scan으로 대체하여 병목 현상을 최소화.

</div>
</details>

<details>
<summary>Redis 캐시를 적용하여 성능 향상</summary>
<div markdown="1">       

- 버거드롭 주문 사이트에서 가장 중요도가 높으면서 조회 빈도도 높은 메인 페이지 음식 목록 조회에 Redis Cache를 적용하여 성능을 향상시켰다. 처음 조회할 때는 650ms 이상이 소요되었지만, 캐시를 적용하고 나서는 평균적으로 60ms 대를 기록하여 약 10배에 가까운 성능 향상이 있었다.
- 상품 단건 조회에서도 처음 조회할 때는 150ms 걸렸지만, 캐시를 적용하고 나서는 50ms 를 기록하여 약 3배의 성능 향상이 있었다.

- 문의하기에서 채팅 메시지 죄회할 때 Redis Cache를 적용하여 성능 향상을 시켰다. 14.16KB에서 적용하기 전 평균 45ms, 캐시 적용 후 평균 15ms로 약 3배 이상 성능이 향상되었음.  

</div>
</details>

<br>

## API 명세서
[API 명세](https://www.notion.so/API-ca5b9b8b15bd447d938655eeba8844e6?pvs=21)

<br>

## ERD

![image (1)](https://github.com/user-attachments/assets/b203ab92-371c-4274-9bd1-7b9b8f74bb37)

<br>
