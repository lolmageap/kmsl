//package com.example.kotlinmongo
//
//import com.example.kotlinmongo.collection.Author
//import com.example.kotlinmongo.collection.Book
//import com.example.kotlinmongo.collection.Status.ACTIVE
//import com.example.kotlinmongo.collection.Status.RETIREMENT
//import com.example.kotlinmongo.extension.*
//import io.kotest.core.spec.style.StringSpec
//import io.kotest.matchers.shouldBe
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.boot.test.context.SpringBootTest
//import org.springframework.data.mongodb.core.MongoTemplate
//import java.math.BigDecimal
//
//@SpringBootTest
//class GroupTest(
//    @Autowired private val mongoTemplate: MongoTemplate,
//) : StringSpec({
//    beforeTest {
//        mongoTemplate.dropCollection(Author::class.java)
//
//        mongoTemplate.insert(
//            Author.of(
//                name = "John",
//                age = 10,
//                weight = 70.0,
//                height = 170f,
//                status = RETIREMENT,
//                books = mutableListOf(
//                    createBook("book1"),
//                    createBook("book2"),
//                ),
//            )
//        )
//        mongoTemplate.insert(
//            Author.of(
//                name = "John",
//                age = 20,
//                weight = 80.0,
//                height = 180f,
//                status = ACTIVE,
//                books = mutableListOf(
//                    createBook("book3"),
//                    createBook("book4"),
//                ),
//            )
//        )
//        mongoTemplate.insert(
//            Author.of(
//                name = "John",
//                age = 30,
//                weight = 90.0,
//                height = 190f,
//                status = ACTIVE,
//                books = mutableListOf(
//                    createBook("book5"),
//                    createBook("book6"),
//                ),
//            )
//        )
//        mongoTemplate.insert(
//            Author.of(
//                name = "John",
//                age = 40,
//                weight = 100.0,
//                height = 200f,
//                status = ACTIVE,
//                books = mutableListOf(
//                    createBook("book7"),
//                    createBook("book8"),
//                ),
//            )
//        )
//    }
//
//    afterTest {
//        mongoTemplate.dropCollection(Author::class.java)
//    }
//
//    "전체에 대한 count 를 구할 수 있다" {
//        val document = document {
//            and { field(Author::name) eq "John" }
//        }
//
//        val count = mongoTemplate.count(document, Author::class)
//        count shouldBe 4
//    }
//
//    "grouping 된 count 를 구할 수 있다" {
//        val document = document {
//            and { field(Author::name) eq "John" }
//        }
//
//        val statusGroup = document.groupBy(Author::status)
//        val countOfGroup = mongoTemplate.count(statusGroup, Author::class)
//        countOfGroup shouldBe mapOf(ACTIVE to 3, RETIREMENT to 1)
//    }
//
//    "전체에 대한 합을 구할 수 있다" {
//        val document = document {
//            and { field(Author::name) eq "John" }
//        }
//
//        val sumOfAge = mongoTemplate.sum(document, Author::age)
//        sumOfAge shouldBe 100
//    }
//
//    "grouping 된 필드에 대한 합을 구할 수 있다" {
//        val document = document {
//            and { field(Author::name) eq "John" }
//        }
//
//        val statusGroup = document.groupBy(Author::status)
//        val sumOfGroup = mongoTemplate.sum(statusGroup, Author::age)
//        sumOfGroup shouldBe mapOf(ACTIVE to 90, RETIREMENT to 10)
//    }
//
//    "전체에 대한 평균을 구할 수 있다" {
//        val document = document {
//            and { field(Author::name) eq "John" }
//        }
//
//        val avgOfAge = mongoTemplate.avg(document, Author::age)
//        avgOfAge shouldBe 25
//    }
//
//    "grouping 된 평균을 구할 수 있다" {
//        val document = document {
//            and { field(Author::name) eq "John" }
//        }
//
//        val statusGroup = document.groupBy(Author::status)
//        val avgOfAge = mongoTemplate.avg(statusGroup, Author::age)
//        avgOfAge shouldBe mapOf(ACTIVE to 30, RETIREMENT to 10)
//    }
//
//    "전체에 대한 최대값을 구할 수 있다" {
//        val document = document {
//            and { field(Author::name) eq "John" }
//        }
//
//        val maxOfAge = mongoTemplate.max(document, Author::age)
//        maxOfAge shouldBe 40
//    }
//
//    "grouping 된 최대값을 구할 수 있다" {
//        val document = document {
//            and { field(Author::name) eq "John" }
//        }
//
//        val nameGroup = document.groupBy(Author::name)
//        val maxOfAge = mongoTemplate.max(nameGroup, Author::age)
//        maxOfAge shouldBe mapOf("John" to 40)
//    }
//
//    "전체에 대한 최소값을 구할 수 있다" {
//        val document = document {
//            and { field(Author::name) eq "John" }
//        }
//
//        val minOfAge = mongoTemplate.min(document, Author::age)
//        minOfAge shouldBe 10
//    }
//
//    "grouping 된 최소값을 구할 수 있다" {
//        val document = document {
//            and { field(Author::name) eq "John" }
//        }
//
//        val nameGroup = document.groupBy(Author::name)
//        val minOfAge = mongoTemplate.min(nameGroup, Author::age)
//        minOfAge shouldBe mapOf("John" to 10)
//    }
//
//    "mongoDB에 데이터를 다른 타입으로 컨버팅 하고 연산을 할 수 있다." {
//        val document = document {
//            and { field(Author::name) eq "John" }
//        }
//
//        val totalHeight = mongoTemplate.sum(document, Author::height, BigDecimal::class)
//        totalHeight.roundOff shouldBe 740.toBigDecimal()
//    }
//})
//
//private val BigDecimal.roundOff: BigDecimal
//    get() = this.setScale(0)
//
//private fun createBook(
//    title: String,
//    price: Long = 10000L,
//    isbn: String = "isbn",
//    description: String? = null,
//) =
//    Book.of(
//        title,
//        price,
//        isbn,
//        description,
//    )