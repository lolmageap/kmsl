package com.example.kotlinmongo

import org.bson.Document

inline fun <reified T> document(
    function: Document.(T) -> Unit,
): Document {
    return Document().apply {
        val instance = T::class.java.getDeclaredConstructor().newInstance()
        function(instance)
    }
}
