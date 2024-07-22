## mongodb dsl

kotlin + spring boot 를 이용한 mongodb dsl 프로젝트 입니다.  
간단한 criteria, bson, aggregation 을 DSL 형태로 사용할 수 있습니다.

현재 실무 에서 사용 하고 있으며 필요한 기능을 추가 하고 있습니다.  
조건이 많고 mongodb 의 조건, 연산이 필요할 때 사용 하고 있습니다.  

spring data jpa와 spring data mongo를 같이 사용할 때 querydsl을 호환성 문제 때문에 사용할 수 없어 criteria, bson을 사용해야 했습니다.  
하지만 criteria, bson을 사용하면 타입 안정성이 떨어지며 코드가 지저분해지고 가독성이 떨어지는 문제가 있어 이를 해결하려 만들었습니다.

### Criteria, QueryDSL Mongo와 Custom MongoDB DSL의 비교

아래는 Author의 name을 in 연산, nickname을 like 연산, age는 between 연산하는 코드입니다.

**Criteria**
```kotlin
fun findAuthors(
    names: List<String>,
    minAge: Int?,
    maxAge: Int?,
    nickname: String?,
): List<Author> {
    val criteriaList = mutableListOf<Criteria>()

    criteriaList.add(Criteria.where("name").`in`(names))

    if (minAge != null && maxAge != null) {
        criteriaList.add(Criteria.where("age").gt(minAge).lt(maxAge))
    } else {
        minAge?.let {
            criteriaList.add(Criteria.where("age").gt(it))
        }
        maxAge?.let {
            criteriaList.add(Criteria.where("age").lt(it))
        }
    }

    nickname?.let {
        criteriaList.add(Criteria.where("nickname").regex(it, "i"))
    }
    val query = if (criteriaList.isNotEmpty()) {
        val criteria = Criteria().andOperator(*criteriaList.toTypedArray())
        Query(criteria)
    } else {
        Query()
    }

    return mongoTemplate.find(query, Author::class.java)
}
```

**QueryDSL Mongo**
```kotlin
private lateinit var authorRepository: JpaRepository<Author, Long>
private val author = QAuthor.author

fun findAuthors(
    names: List<String>,
    minAge: Int?,
    maxAge: Int?,
    nickname: String?,
): List<Author> {
    var predicate = author.name.`in`(names)

    if (minAge != null && maxAge != null) {
        predicate = predicate.and(author.age.gt(minAge).and(author.age.lt(maxAge)))
    } else {
        minAge?.let {
            predicate = predicate.and(author.age.gt(it))
        }
        maxAge?.let {
            predicate = predicate.and(author.age.lt(it))
        }
    }

    nickname?.let {
        predicate = predicate.and(author.nickname.contains(it))
    }

    return authorRepository.findAll(predicate) as List<Author>
}
```

**Custom Mongo DSL**
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
위와 같이 동일한 결과를 반환하는 코드지만 Custom Mongo DSL은 가독성과 오타로 인한 런타임 문제, 타입 안정성까지 챙기게 됩니다.

## 사용법

### document scope
document scope 를 사용하면 basic query 를 생성할 수 있습니다.

```kotlin
val basicQuery = document {}
mongoTemplate.find(basicQuery, Author::class)
```

#### field
document scope 에서 and, or, nor, not을 사용하면 field를 함수 형태로 넘길 수 있습니다.
field 객체는 Expression 을 생성할 수 있습니다.

```kotlin

val basicQuery = document {
    and(
        { field(Author::name) eq "정철희" },
        { field(Author::age) ne 25 },
    )
}

mongoTemplate.find(basicQuery, Author::class)
```

```kotlin
val basicQuery = document {
    or(
        { field(Author::name) `in` ["정철희", "정원희"] },
        { field(Author::age) between (25 to 30) },
    )
}
```

and, or, nor, not 인자 안에 함수 scope 내부는 and 연산으로 처리됩니다.
```kotlin
val basicQuery = document {
    or(
        {
            and(
                { field(Author::name) eq "정철희" },
                { field(Author::age) eq 25 },
                { field(Author::phone) eq "010-1234-5678" },
            )
        },
        {
            and(
                { field(Author::name) eq "정원희" },
                { field(Author::age) eq 30 },
                { field(Author::phone) eq "010-5678-1234" },
            )
        },
    )
}

/**
 * 위의 코드는 아래와 같은 쿼리를 생성합니다.
 * ( 정철희 and 25 and 010-1234-5678 ) 
 * or 
 * ( 정원희 and 30 and 010-5678-1234 )
 */

mongoTemplate.find(basicQuery, Author::class)
```

#### 정렬

정렬은 orderBy 함수를 사용하면 됩니다.
```kotlin
val basicQuery = document {
    and(
        { field(Author::name) eq "정철희" },
    )
}.orderBy(Author::age).desc()
```

아래처럼 여러 필드를 정렬할 수도 있습니다.
```kotlin
val basicQuery = document {
    and(
        { field(Author::name) eq "정철희" },
    )
}.orderBy(Author::age).desc()
    .orderBy(Author::phone).asc()
```

#### grouping
grouping을 사용하면 간단한 통계 쿼리를 생성할 수 있습니다.

```kotlin
// 이름이 정철희인 사람들의 나이의 합
val basicQuery = document {
    and(
        { field(Author::name) eq "정철희" },
    )
}

val aggregate = basicQuery.sumOf { field(Author::age) }
mongoTemplate.sumOfSingle(basicQuery, Author::class)
```

만약 mongodb에 field가 string 타입이어도 숫자로 형변환하여 계산할 수 있습니다. 숫자의 타입이 Int 인지, Long 인지, Double 인지에 설정도 가능합니다.  
기본적으로 Long 타입으로 변환되게 설정되어 있습니다.  
```kotlin
// 이름이 정철희인 사람들의 나이별로 그룹합니다.
// 이 그룹의 핸드폰 번호를 숫자(Double Type)로 형변환한 값의 합 
val basicQuery = document {
    and(
        { field(Author::name) eq "정철희" },
    )
}

val aggregate = basicQuery.groupBy(Author::age).sumOf(Double::class) { field(Author::phone) }
mongoTemplate.sumOfGroup(basicQuery, Author::class)
```

## TODO
- [x] naming 이 아직 미숙한 부분이 많다. naming 을 조금 더 직관적으로 수정하자.
- [x] and operator 와 or operator 와 document scope 를 하나로 합치자.
- [x] 정렬을 구현하여 사용할 수 있도록 하자.
- [ ] aggregation 을 좀 더 편하게 사용할 수 있도록 개선하자.