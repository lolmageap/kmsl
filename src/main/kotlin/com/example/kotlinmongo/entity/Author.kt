package com.example.kotlinmongo.entity

import jakarta.persistence.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field

@Document(collection = "author")
data class Author(
    @Id @Field("_id")
    val id: String? = null,
    val name: String,
    val age: Int,
    val weight: Double,
    val height: Float,
    val status: Status,
)

enum class Status {
    ACTIVE,
    INACTIVE,
}