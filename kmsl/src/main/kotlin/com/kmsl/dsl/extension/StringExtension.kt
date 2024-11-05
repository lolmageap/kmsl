package com.kmsl.dsl.extension

import java.util.*

internal fun String.toSnakeCase() =
    this.replace(Regex("([a-z])([A-Z]+)"), "$1_$2").lowercase(Locale.getDefault())