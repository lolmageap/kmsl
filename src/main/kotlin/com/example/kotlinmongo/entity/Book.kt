package com.example.kotlinmongo.entity

import jakarta.persistence.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
class Book(
    @Id
    val id: String,
    val title: String,
    val description: String,
    val price: Double,
    val isbn: String,
)