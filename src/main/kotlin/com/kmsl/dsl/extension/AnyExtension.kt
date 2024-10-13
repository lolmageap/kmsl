package com.kmsl.dsl.extension

import java.math.BigDecimal

internal fun Any?.toLong(): Long = this.toString().toLong()
internal fun Any?.toInt(): Int = this.toString().toInt()
internal fun Any?.toDouble(): Double = this.toString().toDouble()
internal fun Any?.toFloat(): Float = this.toString().toFloat()
internal fun Any?.toBigDecimal(): BigDecimal = this.toString().toBigDecimalOrNull() ?: BigDecimal.ZERO
internal fun Any?.noReturn() = Unit