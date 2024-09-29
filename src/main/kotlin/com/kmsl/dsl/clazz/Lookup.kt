package com.kmsl.dsl.clazz

import org.bson.Document

data class Lookup(
    val from: String,
    val localField: String,
    val foreignField: String,
    val alias: String,
    val matchDocument: Document,
)