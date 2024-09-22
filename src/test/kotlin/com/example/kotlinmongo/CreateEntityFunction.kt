package com.example.kotlinmongo

import com.example.kotlinmongo.collection.Book
import com.example.kotlinmongo.collection.Receipt

// When creating exception cases, use the copy method
fun createBook(
    title: String,
) =
    Book.of(
        title,
        10000L,
        "isbn",
    )

fun createReceipt(
    card: String,
) =
    Receipt.of(
        card = card,
        price = 10000L,
        date = "2024-09-14",
    )