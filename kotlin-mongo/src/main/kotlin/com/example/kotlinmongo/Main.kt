package com.example.kotlinmongo

fun main() {
    val author = createAuthor()
    criteria<Author> {
        and(Author::name).`is`("John Doe")
        and(Author::age).`is`(30)

        elemMatch {
            and(Book::title).`is`("Book 1")
            and(Book::description).`is`("Description 1")
            and(Book::price).`is`(10.0)
            and(Book::isbn).`is`("1234567890")
        }
    }
}

fun createBook() =
    listOf(
        Book(
            id = "1",
            title = "Book 1",
            description = "Description 1",
            price = 10.0,
            isbn = "1234567890",
        ),
        Book(
            id = "2",
            title = "Book 2",
            description = "Description 2",
            price = 20.0,
            isbn = "0987654321",
        ),
    )

fun createAuthor() =
    Author(
        id = "1",
        name = "John Doe",
        age = 30,
        books = createBook(),
    )