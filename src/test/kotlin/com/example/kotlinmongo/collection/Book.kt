package com.example.kotlinmongo.collection

data class Book(
    var title: String,
    var price: Long,
    var isbn: String,
    var description: String?,
) {
    companion object {
        fun of(
            title: String,
            price: Long,
            isbn: String,
            description: String? = null,
        ) = Book(
            title = title,
            price = price,
            isbn = isbn,
            description = description,
        )
    }
}