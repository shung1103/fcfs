
# 🛍️ First Come First Serve

<br>

## 👉🏻 서비스 소개

    당신만의 위시 리스트를 만드세요!
    FCFS는 당신의 희망 상품 소식을 앱을 켠 상태든 아니든 최대한 빨리 알려 드립니다.
    선착순 구매가 시작되고 나면 필요한 것은 오직 순발력 하나 뿐!
    온갖 누락과 에러 등의 불편함 없는 구매 경험을 선사해 드립니다.

<br>

## ❓기획 의도

    - MSA를 통한 서비스 간 결합도 및 의존도 경감
    - 특정 시간의 선착순 구매로 집중된 대규모 트래픽 처리

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
  <img src="https://img.shields.io/badge/Eureka Gateway-FF4F8B?&style=flat&logo=GateWay&logoColor=white" style="margin-right: 10px;">
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
  <img src="https://img.shields.io/badge/Bucket4j-0052CC?style=flat&logo=bucket4j&logoColor=white" style="margin-right: 10px;">
  <img src="https://img.shields.io/badge/LoadBalancing-8C4FFF?style=flat&logo=Load Balancer&logoColor=white" style="margin-right: 10px;"/>
</div>

<br>
<h2>⚙️서비스 아키텍처</h2>
<p align="center">
  <img src="https://github.com/burger-drop/burger-drop-repo/assets/94231335/6d9e5d0d-3196-4ba6-8464-83a8caf078fa" style="margin-right: 10px;">
</p>


<br>

# ✨주요 기능
1. Spring Cloud Eureka를 사용한 MSA와 멀티 모듈 방식 개발로 각 마이크로 서비스의 독립 운용
2. 모든 API 요청이 WebFlux Gateway를 경유하게 만들어 로드 밸런싱 구현
3. WebFlux Swagger를 통한 모든 마이크로 서비스 API 명세 테스트 및 관리 편의성 추구
4. 헤더 저장 방식을 사용한 JWT로 Spring Security 구현
5. Redis를 활용한 Refresh token 운용 - JWT의 취약점 보완 및 로그인 상태 유지
6. Redis Cache 적용 - 상품 목록 조회, 상품 재고 조회
7. 상품 재입고 시 사용자가 앱 사용 여부에 관계없이 실시간 알림
8. bucket4j를 사용한 rate limiter로 초당 처리율을 제한하여 자원 고갈 방지
9. ribbon과 resilience4j를 사용하여 로드 밸런싱과 서킷 브레이커 구현
10. 비관적 락을 사용하여 누락없는 재고 동시성 처리 구현
11. Docker 사용 - 개발 환경과 동일한 환경으로 안정적인 프로그램 구동 및 배포, 확장 가능

<br>

## 💫 도메인 기능

`회원 가입 / 로그인`
- 유저는 회원가입 시 이메일 인증을 해야한다.
- 소셜 로그인 가능 (구글, 카카오, 네이버)
    - 소셜 로그인 버튼 클릭 → 기존 회원이면 로그인 → 기존 회원 아니면 회원가입
- 헤더 저장 방식 JWT이므로 Redis를 활용한 블랙 리스트 기능으로 로그아웃 구현
- 사용자가 비밀번호 변경 시 모든 기기 로그아웃

`유저`
- 유저 페이지에서 정보 조회, 수정, 탈퇴, 로그아웃 가능
- 유저의 개인정보는 모두 AES 식으로 암호화 상태로 DB에 보관
- 자신의 프로필 화면에서 자신의 주문 내역을 페이지 형태로 조회 가능

`상품`
- 회원 가입이나 로그인 하지 않아도 전체 상품 목록, 단일 상품 정보 조회 가능
- 관리자는 상품 생성, 조회, 수정, 삭제, 재입고 가능
- 상품 재입고 시 해당 상품을 위시 리스트에 등록한 모든 유저들에게 비동기식으로 이메일 알림 전송

`위시 리스트`
- 사용자가 재입고 알림을 받기를 원하는 상품을 위시 리스트에 등록 가능
- 사용자가 위시 리스트의 해당 상품을 구매에 성공할 시 위시 리스트에서 자동으로 제거

`주문`
- 주문 생성, 조회, 삭제 가능. 주문을 수정하려면 주문 취소 후 재주문으로 가능
- 사용자는 자신의 모든 주문 목록을 페이지 형태로 조회 가능
- 사용자는 자신의 주문이 결제가 완료되기 전이나 배달이 완료되고 하루 이내에만 취소 가능
- 1시간마다 전체 주문 목록을 조회하여 주문 상태가 변경되고 24시간이 경과한 주문의 주문 상태를 변경

<br>

# ✔️ 기술적 의사 결정

<details>
<summary>헤더 저장 방식 JWT</summary>
<div markdown="1">

### - 기술의 개념
- JWT란 JSON 객체에 사용자의 인증 정보를 담아 Spring Security가 적용되어 있는 API를 호출할 때 사용되는 토큰
- DB에 세션으로 저장하는 방식, 클라이언트의 브라우저 쿠키에 저장하는 방식, 헤더에 저장하는 방식 등이 존재

### - 왜 이 기술을 선택했는지?
- 현재 Spring Security를 사용하는 대부분의 기업들이 헤더 저장 방식을 사용 중
- 쿠키에 비해 탈취 가능성이 조금 더 낮고 사용자의 동의를 얻을 필요가 적음
- 세션의 경우 DB에 저장되기에 속도가 느리고, 사용자가 늘어날수록 DB 비용이 정비례로 증가
- 토큰 기반으로 다른 로그인 시스템에 접근 및 권한 공유가 가능
- 쿠키의 경우 최근에는 사용자가 쿠키 저장 방식을 거부할 수 있는 사이트가 늘어나고 있어서 제 기능을 못할 가능성이 존재

### - 기술의 장단점
- 장점
    - JWT는 인증에 필요한 모든 정보를 담고 있기 때문에 인증을 위한 별도의 저장소가 불필요
    - 세션(Stateful)과 다르게 서버는 무상태(StateLess)성을 유지 가능
    - 데이터의 위변조를 방지하며 확장성이 우수하여 서드 파티와 연동이 용이
    - OAuth의 경우 소셜 계정을 통해서 다른 웹서비스에 로그인 할 수 있으며 모바일에도 적용 가능
- 단점
    - 쿠키/세션과 다르게 토큰의 길이가 길어, 인증 요청이 많아질수록 네트워크 부하가 심화
    - 쿠키 저장 방식의 경우 쿠키를 삭제하는 방식으로 로그아웃 기능 구현이 가능하나 헤더 저장 방식의 경우 기능 구현 난이도가 상승
    - Payload 자체는 암호화가 되지 않아 중요한 정보는 담을 수 없으며, 탈취당할 시 대처 난이도가 매우 높음
    - 이번 프로젝트의 경우 리프레쉬 토큰을 함께 사용하여 액세스 토큰이 탈취되더라도 문제 없이 동작 가능

</div>
</details>

<details>
<summary>상품 재입고 시 알림으로 메일을 전송함에 있어 rate limiter 적용</summary>
<div markdown="1">

### - 기술의 개념
- rate limiter 처리율 제한 장치는 클라이언트 또는 서비스가 보내는 트래픽 처리율을 제한하기 위한 장치

### - 왜 이 기술을 선택했는지?
- guava → 편리하고 접근성이 좋지만, 동시성 처리에 취약하고 분산 시스템에 부적합
- RateLimitJ → 더 이상 지원 안함. Bucket4j를 사용하도록 공식 문서에 나와 있음
- Bucket4j → 멀티스레딩 환경에서 확장성이 우수하고 높은 동시성을 지원. 로컬메모리 외에도 JDBC, Redis등과 같은 분산 환경의 DB도 지원
- Resilience4j → 요청이 임계치를 넘겼을 때 단순히 거부하는 기능뿐만 아니라, 나중에 실행하기 위해 대기열에 저장하는 두 가지 접근방식을 제공
- Bucket4j를 선택하여 구현하기로 결정 → Resilience4j의 경우 이미 Circuit Breaker로 사용하고 있기에 Rate Limiting 까지 사용할 경우 오버 헤드가 생길 가능성이 높다고 판단

### - 기술의 장단점
- 장점
    - 디도스에 의한 자원 고갈 방지
    - 처리율을 제한함으로써 서버를 많지 않게 두거나, 우선 순위가 높은 API에 더 많은 자원을 할당하는 방식으로 서버 리소스 절감
    - 잘못된 이용 패턴으로 인해 유발된 트래픽을 막아 불필요한 서버 과부하를 방지 가능
- 단점
    - Bucket4j는 라이브러리 형태로 제공되어 단일 서버에 종속이 필수. 따라서 분산환경에서 사용하기 위해서는 Redis와 같은 별도의 서버 구성 필요
    - 다만 이번 프로젝트의 경우 Bucket을 서버끼리 공유할 필요가 없기에 무의미한 단점이라고 판단

</div>
</details>

<details>
<summary>Gateway WebFlux Swagger연동</summary>
<div markdown="1">

### - 기술의 개념
- 개발한 Rest API를 편리하게 문서화 해주고, 이를 통해서 관리 및 제 3의 사용자가 편리하게 API를 호출해보고 테스트 할 수 있는 프로젝트

### - 왜 이 기술을 선택했는지?
- Postman : API의 성공 실패 여부에 대한 모니터링과 여러 환경에서 테스트가 가능하며, 환경 및 문서를 공유하기 위한 다양한 기능들을 제공하여 팀 간의 협업 지원
- WebMVC Swagger : 각 마이크로 서비스마다 API 명세서를 따로 만들어 구현 난이도는 낮지만, 스케일 아웃 경우를 고려하면 오히려 사용하기 어려운 방법
- WebFlux Swagger : 구조적이고, 읽기 쉬운 API 문서를 자동으로 생성하고, 문서에 작성된 스펙에 따라 API의 Request와 Response를 더 빠르게 검증 가능
- 완전한 1인 개발이라 팀 간 협업이 불필요했고, 모든 마이크로 서비스들의 통합 API 명세 작성과 테스트의 편의성을 높이고 싶었기에 Swagger를 사용하기로 결정

### - 기술의 장단점
- 장점
    - 각 마이크로 서비스의 동작과 API 명세를 게이트 웨이에서 통합 관리 가능
    - API 문서가 자동으로 생성되어 개발 비용 감소
    - 모든 마이크로 서비스의 코드의 변경점이 자동으로 적용
    - API 기능 테스트가 편해지고 입력과 결과 예시까지 확인 가능
- 단점
    - CORS 정책 위반 위험성 상승
    - 일반적인 Webmvc Swagger에 비해 어려운 구현 난이도
    - 각 마이크로 서비스 전체를 실행해야 원하는 기능을 시험 가능

</div>
</details>

<details>
<summary>비관적 락을 적용한 선착순 구매</summary>
<div markdown="1">
    
### - 기술의 개념
- 비관적 락이란 트랜잭션이 시작될 때 DB에 Shared Lock 또는 Exclusive Lock을 걸고 시작하는 방법

### - 왜 이 기술을 선택했는지?
- 낙관적 락 : 충돌이 거의 발생하지 않는다고 가정하는 락. DB가 아닌 앱에서 제공하는 버전관리 기능을 통해 구현. 최근 업데이트 과정에서만 락을 점유하기 때문에 락 점유시간을 최소화하여 동시성 처리
- 비관적 락 : 충돌이 자주 발생하는 것을 가정하는 락. DB 단의 Lock을 통해서 동시성을 제어하기 때문에 확실하게 데이터 정합성이 보장. 트랜잭션을 점유하기에 성능이 약간 감소
- Redis 분산 락 : 서버가 여러대인 상황에서 동일한 데이터에 대한 동기화를 보장하기 위해 사용. 트랜잭션 종료 시에 Lock 해제, 세션 관리 등을 수동으로 처리해야 하기 때문에 구현이 복잡
- 특정 시간에 선착순 구매를 진행하는 프로젝트 특성 상 비관적 락을 거는 편이 낙관적 락에 비해 성능 상 유리. 분산 락에 비해서도 성능 차이가 거의 없어 비관적 락을 사용하기로 선택

### - 기술의 장단점
- 장점
    - 가장 높은 수준의 데이터의 일관성과 동시성을 보장
- 단점
    - 성능 저하
    - 데드락 발생 가능성 높음

</div>
</details>

<details>
<summary>resilience4j를 사용한 서킷 브레이커 구현</summary>
<div markdown="1">

### - 기술의 개념
- 서킷 브레이커는 마이크로서비스 간의 호출 실패를 감지하고 시스템의 전체적인 안정성을 유지하는 패턴
- 외부 서비스 호출 실패 시 빠른 실패를 통해 장애를 격리하고, 시스템의 다른 부분에 영향을 주지 않도록 처리
- Resilience4j는 서킷 브레이커 라이브러리로, 서비스 간의 호출 실패를 감지하고 시스템의 안정성을 유지. 다양한 서킷 브레이커 기능을 제공하며, 장애 격리 및 빠른 실패를 통해 복원력 유지

### - 왜 이 기술을 선택했는지?
- hystrix : 공식 github repository에도 Hystrix는 더이상 개발상태가 아닌 유지보수 상태(maintenance mode)라고 공식적으로 명시
- 서킷 브레이커 상태 클로즈드, 오픈, 하프-오픈 상태를 통해 호출 실패를 관리
- Fallback Factory를 통해 호출 실패 시 대체 로직을 제공하여 시스템 안정성 확보
- 서킷 브레이커 상태를 모니터링하고 관리할 수 있는 다양한 도구 제공

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

- FCFS 주문 사이트에서 가장 중요도가 높으면서 조회 빈도도 높은 메인 페이지 상품 목록 조회에 Redis Cache를 적용하여 성능을 향상
- 처음 조회할 때는 650ms 이상이 소요되었지만, 캐시를 적용하고 나서는 평균적으로 60ms 대를 기록하여 약 10배에 가까운 성능 향상

</div>
</details>

<br>

## API 명세서
[API 명세](https://www.notion.so/API-ca5b9b8b15bd447d938655eeba8844e6?pvs=21)

<br>

## ERD

![image (1)](https://github.com/user-attachments/assets/b203ab92-371c-4274-9bd1-7b9b8f74bb37)

<br>
