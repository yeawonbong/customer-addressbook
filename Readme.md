# 고객 주소록 관리 시스템

본 프로젝트는 RESTful API 기반의 고객 주소록 관리 시스템으로, 다음과 같은 설계 원칙과 의도를 바탕으로 구현되었습니다.

## 프로젝트 개요

고객 정보를 효율적으로 관리하고 조회, 추가, 수정 및 삭제 기능을 제공하는 API 기반 시스템입니다. 특히 실제 운영 환경을 고려하여 데이터 접근 및 처리 방식을 최적화하였습니다.

## 주요 설계 특징 및 의도

### 1. 역할 분리 및 명확한 계층 구조

- Controller, Service, DAO 계층 간 역할을 명확히 분리하였습니다.
- Service 계층에서는 비즈니스 로직만 처리하고, 데이터 전환(DTO 변환 등)의 책임을 Mapper로 분리하여 코드의 가독성과 유지보수성을 높였습니다.

### 2. API 설계 현실화

- 원본 요구사항에서는 전화번호를 고유 키로 사용하도록 하였으나, 실무적으로 전화번호는 변경될 수 있는 속성이므로 고유한 ID를 추가하여 키로 사용하였습니다.
- 삭제 API의 파라미터도 단순 검색 키워드를 기반으로 모든 결과를 삭제하도록 설계된 원본 요구사항 대신, 명시적인 ID 리스트를 받아 삭제를 수행하도록 변경하였습니다. 이는 프론트엔드에서 사용자가 명확하게 고객을 선택하고 서버에 요청을 전달하는 구조가 실무적이고 안전한 설계라고 판단했기 때문입니다. 검색 기능은 조회 API에서 제공하며, 삭제는 선택적으로 이루어져야 자원 낭비 및 오삭제 방지가 가능하다고 판단했습니다.
- RESTful 설계 원칙상 DELETE 메서드가 가장 적합하나, HTTP 스펙상 DELETE 요청의 Body 처리가 일관되지 않아 실제 구현상 문제가 있을 수 있습니다. 따라서 현실적인 안정성과 호환성을 고려하여 POST 메서드를 선택하였습니다.

### 3. 데이터 관리 방식 최적화

- 메모리 내 데이터 저장 구조를 마스터 데이터(addressbook)와 슬레이브 데이터(readOnly)로 분리하여, 데이터 접근 부하를 효율적으로 분산 처리하였습니다.
- 데이터가 변경되지 않았을 경우 불필요한 메모리 업데이트 및 파일 저장을 방지하여 시스템 자원 소모를 최소화하였습니다.

### 4. 중앙집중식 예외 처리

- `GlobalExceptionHandler`를 사용하여 예외 처리 로직을 중앙화하였습니다.
- 코드의 중복을 방지하고, 예외 발생 시 일관된 응답 구조를 제공합니다.

### 5. 프로퍼티 파일을 통한 메시지 관리

- 시스템 내 모든 메시지 및 오류 문구를 프로퍼티 파일로 분리하여 중앙에서 관리합니다.
- 수정 및 유지보수 시 프로퍼티 파일 하나에서 모든 메시지를 관리할 수 있도록 하여 관리 포인트를 최소화하고 효율성을 극대화했습니다.

### 6. 데이터 전환 효율화 (Mapper 활용)

- MapStruct 기반의 `CustomerMapper`를 통해 DTO와 Entity 간의 데이터 변환 로직을 분리하여 구현하였습니다.
- 비즈니스 로직과 데이터 변환 로직을 명확히 구분하여 코드의 가독성을 향상시켰습니다.

### 7. API 요청/응답 명확성

- Swagger를 사용하여 모든 API의 요청 및 응답 구조를 명확하게 문서화했습니다.
- 이를 통해 API 사용성을 높이고 프론트엔드와의 협업을 원활하게 하였습니다.

### 8. 로깅 및 인터셉터 적용

- `ApiLoggingInterceptor`를 사용하여 모든 API 호출에 대한 요청 및 응답을 로그로 남깁니다.
- 이를 통해 시스템의 가시성을 높이고 운영 중 발생하는 문제를 빠르게 식별할 수 있습니다.

## 기술 스택

- Java, Spring Boot
- MapStruct, Lombok
- Swagger

## 주요 API

- **고객 조회** (`GET`): ID 기반 상세 조회 및 검색어 기반의 고객 리스트 조회
- **고객 추가** (`POST`): 신규 고객 데이터 등록
- **고객 수정** (`PUT`): 기존 고객 데이터 업데이트
- **고객 삭제** (`POST`): ID 리스트 기반의 고객 데이터 삭제

## 프로젝트 구조

- Controller: API 요청 처리 및 응답 반환
- Service: 비즈니스 로직 처리
- DAO: 데이터 접근 및 관리
- Exception: 전역 예외 처리
- Util: 메시지 및 유효성 검사 관리


---

# API 상세 문서

Swagger(로컬 실행후)에서 API 문서를 확인할 수 있습니다.  
http://localhost:8080/swagger-ui/index.html#/

<details>
<summary><strong> `PUT` /api/customers/{id} - 고객 정보 수정</strong></summary>

## `PUT` /api/customers/{id}
**Summary:** 고객 정보 수정
**Description:** 고객 정보 수정 API  - since: 2024-05-20, 봉예원

### Parameters:
- `id` (path): `integer` 5
### Request Body:
- Content-Type: `application/json`
```json
{
  "address": "서울특별시 마포구",
  "phoneNumber": "010-1234-1234",
  "email": "gildong@testt.com",
  "name": "홍길동"
}
```
### Responses:
- **200**: 수정 성공
```json
{
  "before": {
    "id": 5,
    "address": "전라북도 남원시",
    "phoneNumber": "01000000003",
    "email": "sung@ybong.com",
    "name": "성춘향",
    "idStr": "5"
  },
  "after": {
    "id": 5,
    "address": "서울특별시 마포구",
    "phoneNumber": "01012341234",
    "email": "gildong@testt.com",
    "name": "홍길동",
    "idStr": "5"
  }
}
```
- **400**: 입력값 오류
- **404**: 고객 없음
- **409**: 중복 오류

</details>

<details>
<summary><strong> `GET` /api/customers - 고객 목록 조회</strong></summary>

## `GET` /api/customers
**Summary:** 고객 목록 조회
**Description:** 검색/정렬 가능한 고객 정보 리스트 조회 API  - since: 2024-05-20, 봉예원

### Parameters:
- `reqDto` (query): 
```json
{
  "keyword": "01",
  "filter": "phoneNumber",
  "sortBy": "phoneNumber",
  "sortDir": "asc"
}
```
### Responses:
- **200**: 조회 성공
```json
{
  "count": 3,
  "customers": [
    {
      "id": 5,
      "address": "전라북도 남원시",
      "phoneNumber": "01000000003",
      "email": "sung@ybong.com",
      "name": "성춘향",
      "idStr": "5"
    },
    {
      "id": 7,
      "address": "전라북도 남원시",
      "phoneNumber": "0100000004",
      "email": "lee3@ybong.com",
      "name": "이몽룡",
      "idStr": "7"
    },
    {
      "id": 10,
      "address": "서울특별시 마포구",
      "phoneNumber": "01012301234",
      "email": "gildong@test.com",
      "name": "홍길동",
      "idStr": "10"
    }
  ]
}
```
- **400**: 입력값 오류

</details>

<details>
<summary><strong> `POST` /api/customers - 고객 등록</strong></summary>

## `POST` /api/customers
**Summary:** 고객 등록
**Description:** 고객 등록 API  - since: 2024-05-20, 봉예원

### Request Body:
- Content-Type: `application/json`
```json
{
  "address": "서울특별시 마포구",
  "phoneNumber": "010-1230-1234",
  "email": "gildong@test.com",
  "name": "홍길동"
}
```
### Responses:
- **201**: 등록 성공
  - Content-Type: `*/*`
```json
{
  "customer": {
    "id": 10,
    "address": "서울특별시 마포구",
    "phoneNumber": "01012301234",
    "email": "gildong@test.com",
    "name": "홍길동",
    "idStr": "10"
  }
}
```
- **400**: 입력값 오류
- **409**: 중복 오류

</details>

<details>
<summary><strong> `POST` /api/customers/delete - 고객 삭제</strong></summary>

## `POST` /api/customers/delete
**Summary:** 고객 정보 삭제
**Description:** 여러 고객 정보 삭제 API  - since: 2024-05-20, 봉예원

### Request Body:
- Content-Type: `application/json`
```json
[
  1,2,3
]
```
### Responses:
- **200**: 삭제 성공
  - Content-Type: `*/*`
```json
{
  "deletedCount": 3,
  "deletedCustomers": [
    {
      "id": 1,
      "address": "서울특별시 마포구",
      "phoneNumber": "01012341234",
      "email": "gildong@test.com",
      "name": "홍길동",
      "idStr": "1"
    },
    {
      "id": 2,
      "address": "경기도 성남시",
      "phoneNumber": "0100000001",
      "email": "lee@ybong.com",
      "name": "이몽룡",
      "idStr": "2"
    },
    {
      "id": 3,
      "address": "강원도 강릉시",
      "phoneNumber": "01000000002",
      "email": "lee2@ybong.com",
      "name": "이순신",
      "idStr": "3"
    }
  ]
}
```
- **400**: 입력값 오류
- **404**: 고객 없음

</details>

---

**작성자**: 봉예원

**작성일**: 2025년 5월 20일