## mongodb dsl

kotlin + spring boot 를 이용한 mongodb dsl 샘플 프로젝트 입니다.  
간단한 criteria, bson, aggregation 을 DSL 형태로 사용할 수 있습니다.

현재 실무 에서 사용 하고 있으며 필요한 기능을 추가 하고 있습니다.  
조건이 많고 mongodb 의 조건, 연산이 필요할 때 사용 하고 있습니다.  

mongodb 의 query 를 생성할 때 criteria, bson을 사용하면 코드가 지저분해지고 타입 안정성이 떨어지며 가독성이 떨어지는 문제가 있습니다.  
그리고 spring data jpa와 spring data mongo를 같이 사용할 때 querydsl을 호환성 문제 때문에 사용할 수 없어 만들게 되었습니다.  

## 사용법

### document scope
document scope 를 사용하면 basic query 를 생성할 수 있습니다.

```kotlin
val basicQuery = document {}
mongoTemplate.find(basicQuery, Author::class)
```

#### field
document scope 에서 field 를 사용하면 projection 을 생성할 수 있습니다.

```kotlin

val basicQuery = document {
    field(Author::name) eq "정철희"
    field(Author::age, 25)
}

mongoTemplate.find(basicQuery, Author::class)
```

```kotlin
val basicQuery = document {
    field(Author::name) `in` ["정철희", "정원희"]
    field(Author::age) between (25 to 30)
}
```

#### or
or 연산을 하려고 하면 orOperator를 사용해서 or query를 생성할 수 있습니다.

```kotlin
val basicQuery = orOperator {
    or { field(Author::name) eq "정철희" }
    or { field(Author::age) eq 25 }
}

mongoTemplate.find(basicQuery, Author::class)
```

orOperator에서 or scope 내부는 and 연산으로 처리됩니다.
```kotlin
val basicQuery = orOperator {
    or {
        field(Author::name) eq "정철희"
        field(Author::age) eq 25
    }
    or {
        field(Author::name) eq "정원희"
        field(Author::age) eq 30
    }
}

mongoTemplate.find(basicQuery, Author::class)
```

#### grouping
grouping을 사용하면 간단한 통계 쿼리를 생성할 수 있습니다.

```kotlin
val basicQuery = document {
    field(Author::name) eq "정철희"
}

val aggregate = basicQuery.groupBy().sumOf { Author::age }
mongoTemplate.sumOfSingle(basicQuery, Author::class)
```

만약 mongodb에 field가 string 이라면 sumOfNumber를 사용하면 됩니다.
```kotlin
val basicQuery = document {
    field(Author::name) eq "정철희"
}

val aggregate = basicQuery.groupBy(Author::age).sumOfNumber { Author::phone }
mongoTemplate.sumOfGroup(basicQuery, Author::class)
```