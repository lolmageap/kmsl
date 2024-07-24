# 유스콘'24 발표자 오리엔테이션

## 발표자 소개

안녕하세요. **개선**하는 것을 좋아하는 2년차 개발자 **정철희**입니다.  
**꾸준함**이라는 좋은 습관을 가지기 위해 퇴근 후에도 매일 코드를 작성하며, 제가 작성한 코드에서 개선할 부분들을 찾아 리팩토링하는 것을 좋아합니다.    
저는 현재 **비유바움**에서 백엔드 개발자로 일하고 있으며 주로 **Kotlin**과 **Spring Boot**를 사용하여 정산 서비스를 개발, 운영하고 있습니다.

## 발표 주제 : Kotlin으로 맞춤형 Mongo DSL 개발하면서 배운 것들

### 발표 자료

발표 자료 소스 코드는 https://github.com/lolmageap/mongo-dsl 에서 확인하 실 수 있습니다.

### 개요

가장 먼저 MongoDB DSL을 만들게 된 계기와 목적을 설명하겠습니다.  
제가 있는 비유바움이라는 회사는 정산을 위한 결제 데이터를 MongoDB에 저장하고 있는데 저를 포함한 대부분의 팀원들이 MongoDB에 대한 이해도가 다소 부족하다고 생각했습니다.  
그래서 `MongoDB를 RDBMS ORM처럼 사용할 수 있을까?`라는 고민과 평소 DSL을 만들어보고 싶다는 생각이 겹쳐 MongoDB DSL을 만들게 되었습니다.    
코틀린의 불변성과 고차 함수를 접목하면 정말 재미있겠다는 생각이 들어서 무작정 만들어서 기능과 코드베이스적으로 많이 부족하지만 작은 도전이라고 생각하여 유스콘에 `Kotlin으로 커스텀 MongoDB DSL 만들어서 적용하기`라는 주제로 발표를 준비했습니다.  
이번 발표를 통해 동적으로 MongoDB의 쿼리를 가독성과 타입 안정성을 가진 DSL로 작성하는 방법을 소개하고자 합니다.  

### QueryDSL Mongo, Criteria API 와 비교

먼저 QueryDSL Mongo와 Criteria API를 소개하고 왜 이 두 라이브러리를 사용하지 않고 직접 DSL을 만들었는지에 대해 설명하겠습니다.  
예제로 사용할 코드는 Author의 `name을 in 연산`, `nickname을 like 연산`, `age는 between 연산`하는 코드입니다.

#### QueryDSL Mongo

QueryDSL Mongo는 많은 기능을 제공하지만 설정이 복잡하고 레퍼런스가 생각보다 적었으며 별도의 의존성을 추가해야 한다는 점이 마음에 들지 않았습니다.  
또한 아주 복잡한 쿼리를 만들 때와 동적으로 쿼리를 만들 때 가독성이 떨어지기도 했습니다.  
일단 QueryDSL Mongo는 predicate를 만들어서 재할당 해야하는데 불변성을 강조하는 Kotlin과 잘 어울리지 않았습니다.

```kotlin
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

    return authorRepository.findAll(predicate)
}
```

#### Criteria API

QueryDSL Mongo에 이런 문제들 때문에 Criteria API를 사용하려고 했지만 Criteria API는 타입 안정성과 가독성이 떨어진다는 문제가 있었습니다.

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

#### MongoDB DSL

바로 위의 코드를 MongoDB DSL로 변경하면 아래와 같이 됩니다.  
가독성과 타입 안정성 챙길 수 있게 만들었으며 between 연산을 사용할 때 to 연산자를 사용하여 가독성을 높였습니다.  

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

### 사용법 및 특징

DSL의 구조는 생각보다 정말 단순합니다.  
`document` 함수를 호출하면 `DocumentScope`가 생성되고 `DocumentScope` 내부에서 `and`, `or`, `not`, `nor` 함수를 호출하여 `Document`를 만들 수
있습니다.  
복잡한 조건 연산을 수행할 수 있게 `and`, `or`, `not`, `nor` 함수 내부에서도 또 다른 `and`, `or`, `not`, `nor` 함수를 호출할 수도 있습니다.

#### 가독성

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
                { field(Author::age) eq 23 },
                { field(Author::phone) eq "010-5678-1234" },
            )
        },
    )
}

basicQuery shouldBe BasicQuery(
    "{ \"\$or\" : [ " +
        "{ \"\$and\" : [ { \"name\" : \"정철희\" }, { \"age\" : 25 }, { \"phone\" : \"010-1234-5678\" } ] }, " +
        "{ \"\$and\" : [ { \"name\" : \"정원희\" }, { \"age\" : 23 }, { \"phone\" : \"010-5678-1234\" } ] } " +
    "] }"
)

/**
 * ( 이름 == 정철희 and 나이 == 25 and 번호 == 010-1234-5678 )
 * or
 * ( 이름 == 정원희 and 나이 == 30 and 번호 == 010-5678-1234 )
 */
```

##### 사용시 주의할 점

![img_1.png](image/Document.png)

Document 객체는 Map을 상속받은 클래스이기 때문에 동일한 key가 중복되면 덮어 씌워지게 됩니다.  
그래서 하나의 함수 스코프 내에서 동일한 key(document property 명)를 사용하면 덮어씌워지는 문제가 발생합니다.  

```kotlin
val basicQuery = document {
    and(
        {
            field(Author::name) eq "정철희"
            field(Author::name) eq "정철희 아님"
        },
    )
}

// 예상
basicQuery shouldBe BasicQuery("{ \"\$and\" : [ { \"name\" : \"정철희\" }, { \"name\" : \"정철희 아님\" } ] }")

// 결과
basicQuery shouldBe BasicQuery("{ \"\$and\" : [ { \"name\" : \"정철희 아님\" } ] }")
```

이러한 문제를 해결하기 위해 아래처럼 별도의 조건을 넘길 때 항상 함수 스코프를 넘겨서 새로운 객체를 생성하도록 컨벤션을 수정했습니다.

```kotlin
val basicQuery = document {
    and(
        { field(Author::name) eq "정철희" },
        { field(Author::name) eq "정철희 아님" },
    )
}

// success
basicQuery shouldBe BasicQuery("{ \"\$and\" : [ { \"name\" : \"정철희\" }, { \"name\" : \"정철희 아님\" } ] }")
```

내부 코드를 보면 이해가 되실거라고 생각합니다.

```kotlin
class DocumentOperatorBuilder(
    val document: Document,
) {
    fun and(
        vararg block: Document.() -> (Document),
    ): Document {
        val nonEmptyBlocks = applyNotEmptyFunctions(block)

        if (nonEmptyBlocks.isNotEmpty()) {
            document.append("\$and", nonEmptyBlocks)
        }

        return document
    }

    private fun applyNotEmptyFunctions(
        block: Array<out Document.() -> Document>,
    ): List<Document> = block.mapNotNull {
        val doc = Document().it()
        doc.ifEmpty { null }
    }
}
```

함수식을 하나 넘길 때 마다 새로운 Document 객체를 생성하고 그 객체를 참조하도록 수정했습니다.
이렇게 하면 함수 스코프 내부에서 동일한 키를 사용해도 기존 값을 덮어쓰지 않고 새 Document 객체에 추가하여 이어붙일 수 있습니다.  
참고로 kotlin에서는 함수도 디컴파일 시 익명 클래스로 변환되기 때문에 제네릭에 함수를 넘기는 것도 가능합니다.  

#### 타입 안정성

MongoDB DSL은 타입 안정성을 가지고 있습니다.  
예를 들어 Author 클래스가 아래와 같이 정의되어 있다고 가정해보겠습니다.

```kotlin
@Document
class Author(
    @Id @Field("_id")
    val id: String,
    val name: String,
    val age: Int,
    val weight: Double,
    val height: Float,
)
```

그리고 아래와 같이 쿼리를 작성 할 때 타입 안정성을 챙길 수 있습니다.

```kotlin
val basicQuery = document {
    and(
        { field(Author::name) eq "정철희" },
        { field(Author::age) eq 25 },
        { field(Author::weight) eq 70.0 },
        { field(Author::height) eq 180.0f },
    )
}
```

만약 잘못된 타입을 넣으면 컴파일 에러가 발생합니다.

```kotlin
val basicQuery = document {
    and(
        { field(Author::name) eq "정철희" },
        { field(Author::age) eq "25" }, // 컴파일 에러
        { field(Author::weight) eq 70.0 },
        { field(Author::height) eq 180.0f },
    )
}
```

#### 재사용성

가끔 코드를 작성하게 되면 비슷한 쿼리를 여러번 사용해야 할 때가 있습니다.  
그때 동일한 조건을 재사용할 수 있게도 구현해봤습니다.

```kotlin
val originalQuery = document {
    and(
        { field(Author::name) eq "정철희" },
        { field(Author::age) eq 25 },
    )
}

val weightQuery = originalQuery.where { field(Author::weight) gt 62.0 }
val heightQuery = originalQuery.where { field(Author::height) lt 170f }

weightQuery shouldBe BasicQuery("{ \"\$and\" : [{ \"name\" : \"정철희\"}, { \"age\" : 25}, { \"weight\" : {\"\$gt\" : 62.0}}]}")
heightQuery shouldBe BasicQuery("{ \"\$and\" : [{ \"name\" : \"정철희\"}, { \"age\" : 25}, { \"height\" : {\"\$lt\" : 170.0}}]}")
```

#### grouping

grouping을 사용하면 sum, avg, min, max, count 등 다양한 통계 쿼리를 생성할 수 있습니다.  
아래 코드는 grouping을 명시하진 않았지만 전체를 하나의 그룹으로 묶어서 Author의 나이의 합을 구하는 코드입니다.

```kotlin
val basicQuery = document {
    and(
        { field(Author::name) eq "정철희" },
    )
}

val aggregate = basicQuery.sumOf { field(Author::age) }
mongoTemplate.sumOfSingle(basicQuery, Author::class)
```

grouping을 명시하면 아래와 같이 그룹별로 통계 쿼리를 생성할 수 있습니다.  
그리고 만약 mongodb에 field가 string 타입이어도 숫자로 형변환하여 계산하는 것도 가능합니다.

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

Order 객체 내부 소스 코드입니다.
정렬 부분에 소스 코드도 불변성을 지키기 위해 노력했습니다.

```kotlin
data class Order(
    val basicQuery: BasicQuery,
    val key: KProperty1<*, *>,
) {
    fun asc(): BasicQuery {
        val document = basicQuery.queryObject.copy()
        val existingSort = basicQuery.extractSortObject()
        val newSort = Sort.by(Sort.Direction.ASC, key.name)
        val combinedSort = existingSort.and(newSort)
        val newBasicQuery = BasicQuery(document)
        newBasicQuery.with(combinedSort)
        return newBasicQuery
    }

    fun desc(): BasicQuery {
        val document = basicQuery.queryObject.copy()
        val existingSort = basicQuery.extractSortObject()
        val newSort = Sort.by(Sort.Direction.DESC, key.name)
        val combinedSort = existingSort.and(newSort)
        val newBasicQuery = BasicQuery(document)
        newBasicQuery.with(combinedSort)
        return newBasicQuery
    }

    private fun BasicQuery.extractSortObject() =
        Sort.by(
            this.sortObject.entries.map {
                val sort = when (it.value) {
                    IS_ASC -> Sort.Direction.ASC
                    else -> Sort.Direction.DESC
                }
                Sort.Order(sort, it.key)
            }
        )

    companion object {
        private const val IS_DESC = "-1"
        private const val IS_ASC = "1"
    }
}
```

만약 불변성을 지키지 않았다면 어떤 문제가 있었을까요?  
문제가 발생했던 이전 소스코드이며 예시 테스트 코드도 함께 준비했습니다.

> 문제가 발생한 이전 코드
> Basic Query를 받으면 그 Query에 새로운 Sort를 추가하는 방식으로 구현했습니다.

```kotlin
data class Order(
    val basicQuery: BasicQuery,
    val key: KProperty1<*, *>,
) {
    fun asc(): BasicQuery {
        basicQuery.with(Sort.by(Sort.Direction.ASC, key.name))
        return basicQuery
    }

    fun desc(): BasicQuery {
        basicQuery.with(Sort.by(Sort.Direction.DESC, key.name))
        return basicQuery
    }
}
```

> 문제가 발생한 이전 코드의 테스트 코드

```kotlin
val originalBasicQuery = document {
    and(
        { field(Author::name) eq "정철희" },
    )
}

val firstBasicQuery = originalBasicQuery.orderBy(Author::age).desc()
val secondBasicQuery = originalBasicQuery.orderBy(Author::phone).asc()

// 기대한 값
firstBasicQuery shouldBe BasicQuery("{ \"\$and\" : [ { \"name\" : \"정철희\" } ] }", "{ \"age\" : -1 }") // error

// 실제 값
firstBasicQuery shouldBe BasicQuery(
    "{ \"\$and\" : [ { \"name\" : \"정철희\" } ] }",
    "{ \"age\" : -1, \"phone\" : 1 }"
) // success
```

Q. 왜 위와 같은 문제가 발생할까요?  
A. 해당 문제는 BasicQuery 클래스 내부에서 fieldsObject와 sortObject가 Document 클래스 동일하게 바라보고 있기 때문에 발생했습니다.  

![img.png](image/BasicQuery.png)

이로 인해 secondBasicQuery를 orderBy 함수로 생성할 때 originalBasicQuery 인스턴스 내부의 fieldsObject와 sortObject가 동일한 메모리 주소를 참조하게 되어
firstBasicQuery의 sortObject도 함께 변경되는 문제가 발생합니다.  
그림으로 보면 아래처럼 originalBasicQuery, firstBasicQuery, secondBasicQuery 가 동일한 메모리 주소를 참조하게 되는 문제가 발생합니다.  

// TODO: 여기 그림 그려서 넣어야함.

이 문제를 해결하기 위해 내부적으로 copy() 함수를 구현하여 새로운 Document 객체를 생성하고 각각 그 객체를 참조하도록 수정했습니다.  

### 번외 + 엑셀 다운로드 -> 이건 할지 말지 고민중

## 회고

기능 자체는 저희 회사 프로젝트에 맞춰서 필요한 부분만 구현했지만 조금 더 다양한 기능을 구현해보고 싶다는 생각을 하고 있습니다.  
혼자 만든 DSL이지만 컨벤션에 일관성이 없는 부분이나 불필요한 코드가 많은 부분이 있어서 조금 더 리팩토링이 필요하다고 생각합니다.  
특히 aggregation 부분을 좀 더 다양하게 구현해보고 싶은 욕심이 있습니다.  
