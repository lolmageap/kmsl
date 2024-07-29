# 유스콘'24 발표자 오리엔테이션

타겟 : RDBMS ORM을 사용해본 경험이 있거나 Kotlin에 대해 관심이 있는 개발자
들어가기전에 : 유스콘은 Kotlin 컨퍼런스, MongoDB 컨퍼런스가 아니다.  
Kotlin과 MongoDB를 잘 모르더라도 DSL을 사용하면 쉽게 쿼리를 작성할 수 있다는 것을 보여줘야한다.

## 시간 분배

- 자기 소개, 개요 5분
- 사용법 설명 10분
- 실습 30분
- Q&A 5분

## 사전 준비

- Mongo Atlas에 미리 데이터를 initialize한다.
- 사전에 미리 entity를 만들어 놓아야한다.(Author, Book)
- Test 코드를 작성할 수 있는 환경을 구축한다.

## 목차

- 자기 소개
- DSL을 만들게 된 이유에 대해 설명한다.
- DSL 정의에 대한 간단한 설명한다.
- 기본적인 MongoDSL의 문법을 가르쳐준다.
- 연산, 정렬, 통계 쿼리, 페이지 네이션, 동적 쿼리 등등 다양한 DSL을 작성하고 실행해본다.
- 마무리 멘트를 날리고 Q&A를 진행한다.

## 발표자 소개

안녕하세요. **개선**하는 것을 좋아하는 2년차 개발자 **정철희**입니다.  
현재 **비유바움**이라는 회사에서 백엔드 개발자로 일하고 있으며 주로 **Kotlin**과 **Spring Boot**를 사용하여 정산 서비스를 개발, 운영하고 있습니다.  
저는 **꾸준함**이라는 좋은 습관을 가지기 위해 퇴근 후에도 매일 코드를 작성하며 개선할 부분들을 찾아 리팩토링하는 것을 좋아합니다.  

## 발표 주제 : Kotlin으로 맞춤형 Mongo DSL 개발

### 개요

가장 먼저 MongoDB DSL을 만들게 된 계기와 목적을 설명하겠습니다.  
제가 있는 비유바움이라는 회사는 정산을 위한 결제 데이터를 MongoDB에 저장하고 있는데 저를 포함한 대부분의 팀원들이 MongoDB에 대한 이해도가 다소 부족했습니다.  
그래서 `MongoDB를 RDBMS ORM처럼 사용할 수 있을까?`라는 고민과 평소 DSL을 만들어보고 싶다는 생각이 겹쳐 MongoDB DSL을 만들게 되었습니다.  
기능 자체는 저희 회사 프로젝트 성향에 맞춰서 개발 했기에 소수의 부분만 구현했으며 코드베이스적으로도 많이 부족하지만 작은 도전이라고 생각하여 이렇게
유스콘에 `Kotlin으로 커스텀 MongoDB DSL 만들어서 적용하기`라는 주제로 발표를 준비했습니다.  
이번 발표를 통해 MongoDB의 쿼리를 RDBMS의 ORM처럼 가독성과 타입 안정성을 가진 DSL로 작성하는 방법을 소개하고 같이 코드를 작성해보려합니다.

### Kotlin DSL에 대해 간단하게 설명

DSL은 Domain Specific Language의 약자로 특정 도메인에 특화된 언어를 말합니다.  
제가 이번에 만들어본 MongoDB DSL은 MongoDB의 문법을 모르더라도 RDBMS의 ORM처럼 쉽게 사용하기 위해 Kotlin 문법으로 만든 MongoDB Domain Specific
Language입니다.  
제가 사용한 언어인 Kotlin 이라는 언어는 이런 DSL을 만들기에 매우 쉽고 재밌는 매력적인 언어입니다.

#### MongoDB DSL

예시로 볼 코드는 Author의 `name을 in 연산`, `nickname을 like 연산`, `age는 between 연산` 하는 코드입니다.  
풀어서 말하면 저자의 이름이 `정철희`이거나 `정원희`이고, 별명에 `코틀린`라는 단어가 들어가고, 나이가 25살 이상 30살 이하인 사람을 조회할 수 있는 코드입니다.

```kotlin
fun findAuthors(
    names: List<String>,
    nickname: String?,
    minAge: Int?,
    maxAge: Int?,
): List<Author> {
    val document = document {
        and(
            { field(Author::name) `in` names },
            { field(Author::age) between (minAge to maxAge) },
            { nickname?.let { field(Author::nickname) contains it } },
        )
    }

    return mongoTemplate.find(document, Author::class)
}
```

DSL의 구조는 생각보다 정말 단순합니다.  
`document` 함수를 호출하면 `DocumentScope`가 생성되고 `DocumentScope` 내부에서 `and`, `or`, `not`, `nor` 함수를 호출하여 `Document`를 만들 수
있습니다.  
복잡한 조건 연산을 수행할 수 있게 `and`, `or`, `not`, `nor` 함수 내에서 또 다른 `and`, `or`, `not`, `nor` 함수를 호출할 수도 있습니다.

#### 연산
```kotlin
    "같은 값을 조회합니다." {
        val document = document {
            and(
                { field(Author::name) eq "John" },
                { field(Author::age) eq 18 },
            )
        }
        document shouldBe BasicQuery("{ \"\$and\" : [{ \"name\" : \"John\"}, { \"age\" : 18}]}")
    }

    "같지 않은 값을 조회합니다." {
        val document = document {
            and(
                { field(Author::name) ne "John" },
                { field(Author::age) ne 18 }
            )
        }

        document shouldBe BasicQuery("{ \"\$and\" : [{ \"name\" : {\"\$ne\" : \"John\"}}, { \"age\" : {\"\$ne\" : 18}}]}")
    }

    "큰 값을 조회합니다." {
        val document = document {
            and(
                { field(Author::age) gt 18 },
            )
        }

        document shouldBe BasicQuery("{ \"\$and\" : [{ \"age\" : {\"\$gt\" : 18}}]}")
    }

    "작은 값을 조회합니다." {
        val document = document {
            and(
                { field(Author::age) lt 18 },
            )
        }

        document shouldBe BasicQuery("{ \"\$and\" : [{ \"age\" : {\"\$lt\" : 18}}]}")
    }

    "배열에서 포함하는 값을 조회합니다." {
        val document = document {
            and(
                { field(Author::age) `in` listOf(18, 19, 20) },
            )
        }
    
        document shouldBe BasicQuery("{ \"\$and\" : [{ \"age\" : {\"\$in\" : [18, 19, 20]}}]}")
    }

    "배열에서 포함하지 않는 값을 조회합니다." {
        val document = document {
            and(
                { field(Author::age) notIn listOf(18, 19, 20) },
            )
        }
    
        document shouldBe BasicQuery("{ \"\$and\" : [{ \"age\" : {\"\$nin\" : [18, 19, 20]}}]}")
    }

    "문자열에서 포함하는 값을 조회합니다." {
        val document = document {
            and(
                { field(Author::name) contains "John" },
            )
        }
        
        document shouldBe BasicQuery("{ \"\$and\" : [{ \"name\" : {\"\$regex\" : \"John\"}}]}")
    }
```

#### 통계 쿼리 생성

grouping을 사용하면 sum, avg, min, max, count 등 다양한 통계 쿼리를 생성할 수 있습니다.  
아래 코드는 grouping 대상을 명시하진 않아 조건에 맞는 Author 나이의 전체 합을 구하는 코드입니다.  

```kotlin
val basicQuery = document {
    and(
        { field(Author::name) eq "정철희" },
    )
}

val aggregate = basicQuery.sumOf { field(Author::age) }
mongoTemplate.sumOfSingle(basicQuery, Author::class)
```

grouping을 명시하면 아래와 같이 group 별로 통계 쿼리를 생성할 수 있습니다.  
그리고 만약 mongodb collection에 field가 string 타입이어도 숫자로 형변환하여 계산하는 것도 가능합니다.  

```kotlin
val basicQuery = document {
    and(
        { field(Author::name) eq "정철희" },
    )
}

val aggregate = basicQuery.groupBy(Author::age).sumOf(Double::class) { field(Author::phone) }
mongoTemplate.sumOfGroup(basicQuery, Author::class)
```

#### 정렬

정렬은 orderBy 함수를 사용하면 됩니다.

```kotlin
val basicQuery = document {
    and(
        { field(Author::name) eq "정철희" },
    )
}.orderBy(Author::age).desc()
    .orderBy(Author::phone).asc()
```

## Hands-On 실습

TODO: 간단한 로직들 eq, in, like, gt, lt와 같은 연산자들을 사용하여 MongoDB에 조회를 해볼 예정.  
TODO: 정렬, 통계 쿼리, 페이지 네이션, 동적 쿼리를 생성해 볼 예정.

## 마무리

성장을 원하는 주니어 개발자라면 간단한 나만의 DSL을 만들어본다면 정말 정말 재미있고 성장에 큰 도움이 되는것 같습니다.  
아직 기능적으로 많이 부족해서 join과 같은 aggregate 기능들을 더 많이 지원해보고 싶다는 생각을 하고 있습니다.  
마무리로 `왜 JVM, SPRING 진영은 MongoDB에 대한 ORM이나 DSL이 별로 없고 있어도 왜 지원을 잘 안해줄까?` 라는 생각으로 시작했지만 개발 중간에 `아 없을만 하다` 라는 생각으로 바뀌었습니다!  