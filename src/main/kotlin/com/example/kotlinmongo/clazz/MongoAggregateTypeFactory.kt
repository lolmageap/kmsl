package com.example.kotlinmongo.clazz

import java.math.BigDecimal
import kotlin.reflect.KClass

object MongoAggregateTypeFactory {
    fun <T: Any> exchange(type: KClass<T>): String {
        return when (type) {
            Long::class -> "\$toLong"
            BigDecimal::class -> "\$toDecimal"
            Int::class -> "\$toInt"
            Double::class -> "\$toDouble"
            Float::class -> "\$toFloat"
            else -> "\$toLong"
        }
    }
}