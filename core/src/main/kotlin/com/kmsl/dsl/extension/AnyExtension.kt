package com.kmsl.dsl.extension

import java.math.BigDecimal

internal fun Any?.toLong(): Long = this?.toString()?.replaceAfter(".", "")?.toLongOrNull() ?: 0
internal fun Any?.toInt(): Int = this?.toString()?.replaceAfter(".", "")?.toIntOrNull() ?: 0
internal fun Any?.toDouble(): Double = this?.toString()?.toDoubleOrNull() ?: 0.0
internal fun Any?.toFloat(): Float = this?.toString()?.toFloatOrNull() ?: 0.0f
internal fun Any?.toBigDecimal(): BigDecimal = this?.toString()?.toBigDecimalOrNull() ?: BigDecimal.ZERO
internal val Any?.noReturn: Unit get() = Unit