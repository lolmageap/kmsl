package com.kmsl.dsl

import com.kmsl.dsl.collection.Book
import com.kmsl.dsl.collection.Receipt
import com.kmsl.dsl.collection.Seller

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

fun createSeller(
    authorId: String,
) =
    Seller(
        name = "seller",
        age = 20,
        authorId = authorId,
    )