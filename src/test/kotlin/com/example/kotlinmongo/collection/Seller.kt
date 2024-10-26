package com.example.kotlinmongo.collection

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field

@Document(collection = "seller")
data class Seller(
    val name: String,
    val age: Int,
    val authorId: String,
) {
    @Id @Field("_id")
    val id: String = ObjectId.get().toHexString()

    companion object {
        fun of(
            name: String,
            age: Int,
            authorId: String,
        ) = Seller(
            name = name,
            age = age,
            authorId = authorId,
        )
    }
}