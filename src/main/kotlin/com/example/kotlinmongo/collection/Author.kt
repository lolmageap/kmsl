package com.example.kotlinmongo.collection

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field

@Document(collection = "author")
data class Author private constructor(
    @Id @Field("_id")
    val id: String? = ObjectId.get().toHexString(),
    val name: String,
    val age: Int,
    val weight: Double,
    val height: Float,
    val status: Status,
    val books: MutableList<Book>,
) {
    companion object {
        fun of(
            name: String,
            age: Int,
            weight: Double,
            height: Float,
            status: Status,
            books: MutableList<Book>,
        ) = Author(
            name = name,
            age = age,
            weight = weight,
            height = height,
            status = status,
            books = books,
        )
    }
}

enum class Status {
    ACTIVE,
    INACTIVE,
}