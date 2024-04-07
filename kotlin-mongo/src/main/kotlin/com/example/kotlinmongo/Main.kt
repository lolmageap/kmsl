package com.example.kotlinmongo

fun main() {
    val document = document {
        field(Author::weight) nin listOf(70.0, 80.0)
        field(Author::height) `in` listOf(170.0f, 180.0f)
        field(Address::street) gt "5th Avenue"

        orBuilder {
            or {
                field(Author::name) eq "Jane Doe"
            }

            or {
                field(Author::age) `in` listOf(30, 40)
            }
        }
    }

    println(document)
}