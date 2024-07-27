package com.example.kotlinmongo.clazz

import java.math.BigDecimal
import java.util.*
import kotlin.reflect.KClass

object MongoTypeMapper {
    fun <T : Any> exchange(
        type: KClass<T>,
    ) =
        when (type) {
            Long::class -> "\$toLong"
            BigDecimal::class -> "\$toDecimal"
            Int::class -> "\$toInt"
            Double::class -> "\$toDouble"
            String::class -> "\$toString"
            Date::class -> "\$toDate"
            Boolean::class -> "\$toBool"
            else -> "\$toLong"
        }
}