package com.example.kotlinmongo.collection

data class AuthorAndSeller(
    val authorId: String,
    val authorName: String,
    val authorAge: Int,
    val authorWeight: Double,
    val authorHeight: Float,
    val authorStatus: Status,
    val books: MutableList<Book>,
    val receipt: Receipt?,
    val sellerId: String,
    val sellerName: String,
    val sellerAge: Int,
    val sellerAuthorId: String,
) {
    constructor(
        author: Author,
        seller: Seller,
    ) : this(
        authorId = author.id!!,
        authorName = author.name,
        authorAge = author.age,
        authorWeight = author.weight,
        authorHeight = author.height,
        authorStatus = author.status,
        books = author.books,
        receipt = author.receipt,
        sellerId = seller.id!!,
        sellerName = seller.name,
        sellerAge = seller.age,
        sellerAuthorId = seller.authorId,
    )
}
