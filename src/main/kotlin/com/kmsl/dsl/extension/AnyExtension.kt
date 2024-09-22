package com.kmsl.dsl.extension

import java.math.BigDecimal

fun Any?.toLong(): Long = this.toString().toLong()
fun Any?.toInt(): Int = this.toString().toInt()
fun Any?.toDouble(): Double = this.toString().toDouble()
fun Any?.toFloat(): Float = this.toString().toFloat()
fun Any?.toBigDecimal(): BigDecimal = this.toString().toBigDecimalOrNull() ?: BigDecimal.ZERO