<div align=center>
  
# Welcome to Ezfee
</div>

> Ezfee Back-End Server Project
<div align=center>
  <img src="https://github.com/PRIETAG/PRIETAG_BE/assets/57251982/38c6ff31-8cf8-48b1-b717-8bf55f840ce3.png" width="600" height="200"/>
</div>

<div align=center>

# SaaS회사들을 위한 가격표 생성 관리 플랫폼 ,이지피
</div>

## 기술스택
<p>
  <img src="https://img.shields.io/badge/-SpringBoot-blue"/>&nbsp
  <img src="https://img.shields.io/badge/-JPA-red"/>&nbsp
  <img src="https://img.shields.io/badge/-MySQL-yellow"/>&nbsp
  <img src="https://img.shields.io/badge/-JWT-blue"/>&nbsp
  <img src="https://img.shields.io/badge/-AWS-orange"/>&nbsp
  <img src="https://img.shields.io/badge/-Swagger-black"/>&nbsp
  <img src="https://img.shields.io/badge/-SpringSecurity-green"/>&nbsp
  <img src="https://img.shields.io/badge/-Mockito-violet"/>&nbsp
</p>

## 개발환경

- backend
  - java11
  - gradle
  - spring-boot 2.7.12

## 시스템 구성도

 <img src="https://github.com/PRIETAG/PRIETAG_BE/assets/57251982/1dd855cc-f8fb-4fcd-9665-c070537a0945.png" width="806" height="608"/>
 
- 배포 프로세스
![image](https://github.com/PRIETAG/PRIETAG_BE/assets/57251982/fb5cafda-404c-407f-9804-0db09cb91722)

## ERD

<div align=center>
 <img src="https://github.com/PRIETAG/PRIETAG_BE/assets/57251982/b1a82c4c-dac4-4654-a4bf-de156febf873.png" width="806" height="700"/>
</div>

## 핵심 기능
- 카카오 로그인
- 템플릿 관리(생성, 수정, 복사, 삭제, 퍼블리싱)
  - 가격표 템플릿을 생성할 때 사용할 로고 이미지 삽입가능
  - 사용하고 싶은 테마 색상 선택 후 자유롭게 템플릿 생성
  - 사용하고 싶을 경우 퍼를리싱
- Kpi 지표 확인
  - 오늘의 kpi 정보 확인
  - 선택한 날짜로 기간별, 템플릿 별 kpi 확인
  - 2개의 템플릿 kpi 비교 

## 트러블 슈팅
- **"cp: target 'deploy/application.jar' is not a directory" 에러**
  - 원인 : SpringBoot 2.5.0 이상버전에서 따로 설정 변경 없이 사용하면 Gradle을 통한 빌드시, BootJar와 Jar task가 모두 실행되어 jar파일이 2개가 생성됨
  - 해결 : build.gradle에 
          ```
    jar {
                enabled = false
              } ```
   를 추가해 Jar task를 스킵

- **HQL(Hibernate Query Language)에서는 "limit" 키워드를 지원하지 않음**
  - 해결 : pageable을 이용해 0페이지에 1개만 나오게 해서 해결
- **쿼리문에서 pageable과 함께 Join fetch사용시 오류가 발생**
  - 원인 : pageable을 Count쿼리를 자동으로 생성해주는데 그런 Query를 만들기 어려워 나온 문제
  - 해결 : CountQuery를 뒤에 직접 작성
- **서버를 다 띄우고 나서 나중에 사이트 들어가보니 갑자기 502에러 발생, 확인 해보니 ALB에서 health Check가 unhealthy**
  - 원인 : 서버가 백엔드 서버라 바로 200을 날리는 api가 없고, health Check가 / api로 설정 되어있어 404 Not Found 에러가 발생
  - 해결 : 200을 날려주는 api를 만들고 health check 주소를 해당api로 변경
- **실행시 원인모를 SQL서버 에러 발생**
  - 원인 : 사용하는 컬럼중에 예약어가 있어서 발생
  - 해결 : 예약어가 아닌 이름으로 변경

## Member
| 포지션 | 이름 | 담당 |
| --- | --- | --- |
| `BE` `팀장` | 이윤형 | - CI / CD<br/>- 인프라(S3, RDS)<br/>- 템플릿 생성 / 조회<br/>- KPI 로그 저장<br/>- KPI 조회<br/>-로고 저장(S3) |
| `BE` | 신효원 | - 템플릿 버전 생성 / 삭제<br/>- 템플릿 복제 / 퍼블리싱 / 조회<br/>- 템플릿 조회 / 삭제 |
| `BE` | 이경환 | - 회원가입 / 로그인 -JWT |


