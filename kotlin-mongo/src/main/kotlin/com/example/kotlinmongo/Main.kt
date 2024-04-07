package com.example.kotlinmongo

fun main() {
    val document = document {
        field(Author::weight) gteAndLt (70.0 to 80.0)
        field(Author::height) `in` listOf(170.0f, 180.0f)

        field(Address::street) gt "5th Avenue"
        field(Address::city) between ("New York" to "Los Angeles")
        field(Address::zipcode) contains "93"

        field(Company::name) nin listOf("Apple", "Google")
        field(Company::ceo) startsWith "John"

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