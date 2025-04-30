<h1 align="center">API_Tests - 외부 공공 API 활용 실습 프로젝트</h1>

---

> 본 프로젝트는 학습 및 포트폴리오 목적의 예제이며, 외부 API는 각 플랫폼의 사용 약관을 따릅니다.
> 다양한 실전 API 활용 및 데이터 파싱 경험을 위한 Java 기반 실습 프로젝트입니다.  
> XML, JSON, 크롤링(셀레니움) 등의 방식으로 DB 저장 직전의 원시적인 응답 형태를 분석할 수 있습니다.

---

## 사용한 API(클릭 시 이동)

- [식약처 건강기능식품 원재료 API](https://www.foodsafetykorea.go.kr/api/)
- [공공데이터포털 - e약은요 API](https://www.data.go.kr/data/15075057/openapi.do)
- [NAVER Book Search API](https://developers.naver.com/docs/serviceapi/search/book/book.md)
- [동행복권 사이트 (크롤링)](https://dhlottery.co.kr/gameResult.do)
  
---

## 주요 파일 설명

### NAVER 도서 검색

- `NaverBookApiCaller.java`  
  → 도서 검색 키워드로 NAVER API 호출 → `Book` 객체로 매핑

- `Book.java`  
  → 도서 제목/저자/출판사/가격 등 응답 데이터 모델 정의

### 건강기능식품 정보

- `FoodSafetyApiExample.java`  
  → 건강기능식품 원재료 검색 및 XML 파싱

- `HealthProductInfo.java`  
  → 기능성 정보, 섭취 주의사항 등 상세 항목 출력 예제

### e약은요 API

- `ApiExplorer.java`  
  → 검색 결과를 XML 형태로 가져오고 원시 응답 확인

### 로또 당첨 정보 크롤링

- `crawlerApi.java`  
  → Selenium 기반 크롤링 / 1~n회차 로또 정보 수집 /  
    → DB 저장에 활용 가능(현재 코드엔 없음(`lotto_history`, `lotto_prize_details`)

---

## 실행 예시
### ⚠ 실행 전 주의사항 :.env 또는 VM 옵션을 통해 필요한 API 키 또는 DB 비밀번호를 설정해 주세요.

```cmd
# Naver Book 검색
> java -cp build/libs API.naverbook.NaverBookApiCaller
[0001] - 전래동화집 | 저자: 김철수 | 출판사: 민들레출판사 | ISBN: 123456 | 가격: 12000원
...

# 건강기능식품 원재료
> java -cp build/libs api.healthfunctionproductinfo.FoodSafetyApiExample
품목명: 홍삼진액
제조사: 건강한사람들
기능성: 면역력 증진, 피로 개선
...

# 로또 전체 회차 크롤링 (Selenium)
> java -cp build/libs api.selenium.crawlerApi
[10회차] 저장 완료
[10회차] 상세 등수별 정보:
2등 | 당첨자: 10명 | 당첨금: 51,000,000원
...


