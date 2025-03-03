# KMSL

`KMSL` is a Kotlin MongoDB DSL library.  
Spring Data MongoDB is supported in Kotlin DSL form.  
It was created to solve dynamic queries and complex operations.

## Dependencies

It requires JDK version 17 or higher

add the following to your build.gradle.kts file:

```kotlin
plugins {
    kotlin("plugin.noarg") version "your-kotlin-version"
}

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation("com.github.lolmageap:kmsl:1.0.2")
    
    // please add the following dependencies
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
}

noArg {
    annotation("org.springframework.data.mongodb.core.mapping.Document")
    annotation("com.kmsl.dsl.annotation.Projection")
    annotation("com.kmsl.dsl.annotation.EmbeddedDocument")
}
```

# Usage

## Document Scope

By using the document scope, you can create basic queries.

```kotlin
val basicQuery = document {}
mongoTemplate.find(basicQuery, Author::class)
```

### Field

In the document scope, using and, or, and nor, you can pass fields as function arguments.  
The field object can generate an Expression.

Below is an example where the top-level scope is created with an AND operator.

```kotlin
val basicQuery = document {
    field(Author::name) eq "cherhy"
    field(Author::age) ne 25
}

val basicQuery2 = document(AND) {
    field(Author::name) eq "cherhy"
    field(Author::age) ne 25
}

basicQuery shouldBe basicQuery2
mongoTemplate.find(basicQuery, Author::class)
```

Below is an example where the top-level scope is created with an OR operator.

```kotlin
val basicQuery = document(OR) {
    field(Author::name) `in` ["cherhy", "wonny"]
    field(Author::age) between (25 to 30)
}
```

For improved readability, you can use it as shown below.

```kotlin
val basicQuery = document {
    or {
        field(Author::name) `in` ["cherhy", "wonny"]
        field(Author::age) between (25 to 30)
    }
}
```

Below is an example where the top-level scope is created with an NOR operator.

```kotlin
val basicQuery = document(NOR) {
    field(Author::name) `in` ["cherhy", "wonny"]
    field(Author::age) between (25 to 30)
}
```

Complex operations can be handled as shown below.

```kotlin
val basicQuery = document(OR) {
    and {
        field(Author::name) eq "cherhy"
        field(Author::age) eq 25
        field(Author::phone) eq "010-1234-5678"
    }
    and {
        field(Author::name) eq "wonny"
        field(Author::age) eq 30
        field(Author::phone) eq "010-5678-1234"
    }
}

val basicQuery2 = document {
    or {
        and {
            field(Author::name) eq "cherhy"
            field(Author::age) eq 25
            field(Author::phone) eq "010-1234-5678"
        }
        and {
            field(Author::name) eq "wonny"
            field(Author::age) eq 30
            field(Author::phone) eq "010-5678-1234"
        }
    }
}

/**
 * The code above generates a query like the one shown below
 *
 * ( cherhy and 25 and 010-1234-5678 )
 * or
 * ( wonny and 30 and 010-5678-1234 )
 */

mongoTemplate.find(basicQuery, Author::class)
```

### Embedded Document

You can use the embeddedDocument function to handle embedded documents.

```kotlin
@EmbeddedDocument
data class Book(
    var title: String,
    var price: Long,
    var isbn: String,
    var description: String?,
)
```

It is used when searching for embedded documents within a collection.  
You can declare it as shown below.

```kotlin
val basicQuery = document {
    embeddedDocument(Author::books)
}
```

#### Using Embedded Document Fields

You can use fields inside an embedded document declared with embeddedDocument as conditions.  
When performing operations on a single embedded document field, you can use it as shown below.  
Within the where function, you can also use and, or, and nor functions.

```kotlin
val basicQuery = document {
    field(Author::name) eq "cherhy"

    embeddedDocument(Author::receipt) where {
        field(Receipt::card) eq "toss"
        field(Receipt::price) gte 10000L
    }
}

mongoTemplate.find(basicQuery, Author::class)
```

#### ElemMatch

When performing operations on an array field of embedded documents, you can specify conditions using the elemMatch
function as shown below.  
Within the elemMatch function, you can also use and, or, and nor functions.

```kotlin
val basicQuery = document {
    field(Author::name) eq "cherhy"

    embeddedDocument(Author::books) elemMatch {
        field(Book::title) contains "kotlin"
        field(Book::price) gt 10000
    }
}

mongoTemplate.find(basicQuery, Author::class)
```

### Sort

You can use the order function to handle sorting.

```kotlin
val basicQuery = document {
    field(Author::name) eq "cherhy"
} order {
    field(Author::age) by DESC
}
```

You can also sort by multiple fields as shown below.

```kotlin
val basicQuery = document {
    field(Author::name) eq "cherhy"
} order {
    field(Author::age) by DESC
    field(Author::phone) by ASC
}
```

### Grouping

To calculate the total sum, you can write the code as shown below.

```kotlin
val basicQuery = document {
    field(Author::name) eq "cherhy"
} sum {
    field(Author::age) alias "sumOfAge"
}

mongoTemplate.aggregate(basicQuery, Author::class)
```

Simple statistical queries support extension functions like mongoTemplate.sum

```kotlin
val basicQuery = document {
    field(Author::name) eq "cherhy"
}

mongoTemplate.sum(basicQuery, Author::age)
```

Even if a field in the actual MongoDB is of type String, you can perform calculations by converting it to a number.  
Below is an example of converting to a number type when summing in MongoDB.

```kotlin
val statusGroup = document {
    field(Author::name) eq "cherhy"
} group {
    field(Author::status)
} sum {
    field(Author::phone) type Double::class alias "sumOfPhone"
}

mongoTemplate.aggregate(statusGroup, Author::class)
```

You can also group by multiple groups.

```kotlin
val statusAndAgeGroup = document {
    field(Author::name) eq "cherhy"
} group {
    field(Author::status)
    field(Author::age)
} sum {
    field(Author::phone) alias "sumOfPhone"
}

mongoTemplate.aggregate(statusAndAgeGroup, Author::class)
```

or

```kotlin
val statusAndAgeGroup = document {
    field(Author::name) eq "cherhy"
} group {
    field(Author::status) and field(Author::age)
} sum {
    field(Author::phone) alias "sumOfPhone"
}

mongoTemplate.aggregate(statusAndAgeGroup, Author::class)
```

You can also calculate complex conditions and statistics in a single query, as shown below.

```kotlin
document {
    field(Author::age) lt 30
    embeddedDocument(Author::books) elemMatch {
        field(Book::price) type Double::class gte 10000.0
        field(Book::description) startsWith "ko"
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

### Update

You can use the update function to update the document.

```kotlin
val document = document {
    field(Author::name) eq "cherhy"
} order {
    field(Author::age) by DESC
} update {
    field(Author::age) set 30
}

mongoTemplate.updateFirst(document, Author::class)
```

You can also update multiple documents.

```kotlin
val document = document {
    field(Author::name) eq "cherhy"
} update {
    field(Author::age) set 30
}

mongoTemplate.updateAll(document, Author::class)
```

You can also use the unset function to unset a field.

```kotlin
val document = document {
    field(Author::name) eq "cherhy"
} update {
    field(Author::age) unset Unit
} order {
    field(Author::age) by DESC
}

mongoTemplate.updateFirst(document, Author::class)
```

### Delete

You can use the delete function to delete the document.

```kotlin
val document = document {
    field(Author::name) eq "cherhy"
} order {
    field(Author::age) by DESC
}

mongoTemplate.deleteFirst(document, Author::class)
```

You can also delete multiple documents.

```kotlin
val document = document {
    field(Author::name) eq "cherhy"
}

mongoTemplate.deleteAll(document, Author::class)
```

### Join & Projection

You can use the projection function to project fields.

```kotlin
val document = document {
    field(Author::name) eq "cherhy"
} where {
    field(Author::age) eq 25
} join {
    field(Author::id) eq field(Seller::authorId)
} projection {
    constructor(AuthorAndSeller::class)
}

mongoTemplate.aggregate(document, Author::class)
```

But currently, the join can only be performed on a single collection, and the projection is also limited in the
following way:

```kotlin
@Projection
data class AuthorAndSeller(
    val author: Author,
    val seller: Seller
)
```

or

```kotlin
@Projection
data class AuthorAndSeller(
    val authorId: String,
    val authorName: String,
    val authorAge: Int,
    val authorWeight: Double,
    val authorHeight: Float,
    val authorStatus: Status,
    val books: MutableList<Book>,
    val receipt: Receipt?,
    val sellerId: String,
    val sellerName: String,
    val sellerAge: Int,
    val sellerAuthorId: String,
) {
    constructor(
        author: Author,
        seller: Seller,
    ) : this(
        authorId = author.id,
        authorName = author.name,
        authorAge = author.age,
        authorWeight = author.weight,
        authorHeight = author.height,
        authorStatus = author.status,
        books = author.books,
        receipt = author.receipt,
        sellerId = seller.id,
        sellerName = seller.name,
        sellerAge = seller.age,
        sellerAuthorId = seller.authorId,
    )
}
```