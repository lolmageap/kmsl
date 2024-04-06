package com.example.kotlinmongo

import org.bson.Document
import kotlin.reflect.full.createInstance

inline fun <reified T: Any> document(
    function: Document.(T) -> Unit,
): Document {
    return Document().apply {
        val instance = T::class.createInstance()
        function(instance)
    }
}
