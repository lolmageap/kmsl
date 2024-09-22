package cherhy.mongo.dsl

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class KotlinMongoApplication

fun main(args: Array<String>) {
	runApplication<cherhy.mongo.dsl.KotlinMongoApplication>(*args)
}
