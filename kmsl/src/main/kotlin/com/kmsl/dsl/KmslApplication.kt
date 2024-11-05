package com.kmsl.dsl

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class KmslApplication

fun main(args: Array<String>) {
	runApplication<KmslApplication>(*args)
}
