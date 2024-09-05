package com.example.kotlinmongo.clazz

import org.springframework.data.mapping.toDotPath
import kotlin.reflect.KProperty1

class EmbeddedDocument private constructor(
    private val property: KProperty1<*, *>,
) {
    val name: String
        get() = property.toDotPath()

    companion object {
        fun of(
            property: KProperty1<*, *>,
        ) = EmbeddedDocument(property)
    }
}