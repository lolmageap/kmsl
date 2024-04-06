package com.example.kotlinmongo

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
class Author(
    @Id
    val id: String,
    val name: String,
    val age: Int,
    val books: List<Book>,
)