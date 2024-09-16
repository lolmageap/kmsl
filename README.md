# mongodb dsl

kotlin + spring boot 를 이용한 mongodb dsl 프로젝트 입니다.  
간단한 criteria, bson, aggregation 을 DSL 형태로 사용할 수 있습니다.

현재 실무 에서 사용 하고 있으며 필요한 기능을 추가 하고 있습니다.  
조건이 많고 mongodb 의 조건, 연산이 필요할 때 사용 하고 있습니다.

# 사용법

## document scope

document scope 를 사용하면 basic query 를 생성할 수 있습니다.

```kotlin
val basicQuery = document {}
mongoTemplate.find(basicQuery, Author::class)
```

### field

document scope 에서 and, or, nor을 사용하면 field를 함수 형태로 넘길 수 있습니다.  
field 객체는 Expression 을 생성할 수 있습니다.

아래는 최상단의 scope가 AND 연산자로 생성된 예시입니다.

```kotlin
val basicQuery = document {
    field(Author::name) eq "정철희"
    field(Author::age) ne 25
}

val basicQuery2 = document(AND) {
    field(Author::name) eq "정철희"
    field(Author::age) ne 25
}

basicQuery shouldBe basicQuery2

mongoTemplate.find(basicQuery, Author::class)
```

아래는 최상단의 scope가 OR 연산자로 생성된 예시입니다.

```kotlin
val basicQuery = document(OR) {
    field(Author::name) `in` ["정철희", "정원희"]
    field(Author::age) between (25 to 30)
}
```

아래는 최상단의 scope가 NOR 연산자로 생성된 예시입니다.

```kotlin
val basicQuery = document(NOR) {
    field(Author::name) `in` ["정철희", "정원희"]
    field(Author::age) between (25 to 30)
}
```

and, or, nor, not 인자 안에 함수 scope 내부는 and 연산으로 처리됩니다.

```kotlin
val basicQuery = document(OR) {
    and {
        field(Author::name) eq "정철희"
        field(Author::age) eq 25
        field(Author::phone) eq "010-1234-5678"
    }
    and {
        field(Author::name) eq "정원희"
        field(Author::age) eq 30
        field(Author::phone) eq "010-5678-1234"
    }
}

/**
 * 위의 코드는 아래와 같은 쿼리를 생성합니다.
 * ( 정철희 and 25 and 010-1234-5678 )
 * or
 * ( 정원희 and 30 and 010-5678-1234 )
 */

mongoTemplate.find(basicQuery, Author::class)
```

### embedded document

collection 내부의 embedded document를 검색할 때 사용합니다.  
아래와 같이 선언 할 수 있습니다.

```kotlin
val basicQuery = document {
    embeddedDocument(Author::books)
}
```

#### 조건

embeddedDocument로 선언된 embedded document 내부의 필드를 조건으로 사용할 수 있습니다.    
단일 embedded document 필드에 대한 연산을 할 때는 아래와 같이 사용할 수 있습니다.
where 함수 안에서 and, or, nor 함수를 사용할 수도 있습니다.

```kotlin
val basicQuery = document {
    field(Author::name) eq "정철희"
    embeddedDocument(Author::receipt) where {
        field(Receipt::card) eq "신한"
        field(Receipt::price) gte 10000L
    }
}

mongoTemplate.find(basicQuery, Author::class)
```

#### elemMatch

embedded document 배열 필드에 대한 연산을 할 때는 아래와 같이 elemMatch 함수를 사용해서 조건을 지정할 수 있습니다.
elemMatch 함수 안에서 and, or, nor 함수를 사용할 수도 있습니다.

```kotlin
val basicQuery = document {
    field(Author::name) eq "정철희"
    embeddedDocument(Author::books) elemMatch {
        field(Book::title) contains "코틀린"
        field(Book::price) gt 10000
    }
}

mongoTemplate.find(basicQuery, Author::class)
```

### 정렬

정렬은 orderBy 함수를 사용하면 됩니다.

```kotlin
val basicQuery = document {
    field(Author::name) eq "정철희"
} order {
    field(Author::age) by DESC
}
```

아래처럼 여러 필드를 정렬할 수도 있습니다.

```kotlin
val basicQuery = document {
    field(Author::name) eq "정철희"
} order {
    field(Author::age) by DESC
    field(Author::phone) by ASC
}
```

### grouping

전체의 합을 구할 때 아래처럼 코드를 작성할 수 있습니다.

```kotlin
val basicQuery = document {
    field(Author::name) eq "정철희"
} sum {
    field(Author::age) alias "sumOfAge"
}

mongoTemplate.aggregate(basicQuery, Author::class)
```

간단한 통계 쿼리는 mongoTemplate.sum과 같은 확장 함수를 지원해줍니다.

```kotlin
val basicQuery = document {
    field(Author::name) eq "정철희"
}

mongoTemplate.sum(basicQuery, Author::age)
```

만약 실제 mongoDB에 field가 String 타입이어도 숫자로 형변환하여 계산할 수 있습니다.    
아래는 sum 할 때 mongoDB에서 숫자 타입으로 컨버팅하는 예시입니다.

```kotlin
val statusGroup = document {
    field(Author::name) eq "정철희"
} group {
    field(Author::status) by SINGLE
} sum {
    field(Author::phone) type Double::class alias "sumOfPhone"
}

mongoTemplate.aggregate(statusGroup, Author::class)
```

여러 group으로 그룹핑할 수도 있습니다.

```kotlin
val statusAndAgeGroup = document {
    field(Author::name) eq "정철희"
} group {
    field(Author::status) and field(Author::age)
} sum {
    field(Author::phone) alias "sumOfPhone"
}

mongoTemplate.aggregate(statusAndAgeGroup, Author::class)
```

아래와 같이 복잡한 조건과 통계를 한번에 구할 수도 있습니다.

```kotlin
document {
    field(Author::age) eq 30
    embeddedDocument(Author::books) elemMatch {
        field(Book::price) exists false
        field(Book::description) startsWith "test"
    }
} order {
    field(Author::age) by DESC
    field(Author::weight) by ASC
} group {
    field(Author::status) and field(Author::age)
} sum {
    field(Author::age) alias SUM_FIELD
} average {
    field(Author::weight) alias AVERAGE_FIELD
} max {
    field(Author::height) alias MAX_FIELD
} min {
    field(Author::height) alias MIN_FIELD
} count {
    field(Author::id) alias COUNT_FIELD
}

mongoTemplate.aggregate(document, Author::class)
```