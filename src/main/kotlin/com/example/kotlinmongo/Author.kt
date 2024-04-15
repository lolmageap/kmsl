package com.example.kotlinmongo

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.io.Serializable

@Document
class Author(
    @Id @Field("_id")
    val id: String,
    val name: String,
    val age: Int,
    val weight: Double,
    val height: Float,

    @DBRef
    val books: List<Book>,

    val address: Address,
    val company: Company,
)

@Document
data class Address(
    val city: String,
    val street: String,
    val zipcode: String,
): Serializable

@Document
data class Company(
    val name: String,
    val ceo: String,
): Serializable