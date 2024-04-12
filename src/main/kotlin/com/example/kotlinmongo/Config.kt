package com.example.kotlinmongo

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver
import org.springframework.data.mongodb.core.convert.MappingMongoConverter
import org.springframework.data.mongodb.core.mapping.MongoMappingContext

@Configuration
class Config {

    @Bean
    fun mongoTemplate(): MongoTemplate {
        val factory = SimpleMongoClientDatabaseFactory("mongodb://localhost:27017/test")
        val converter = MappingMongoConverter(
            DefaultDbRefResolver(
                SimpleMongoClientDatabaseFactory("mongodb://localhost:27017/test")
            ),
            MongoMappingContext(),
        )
        val template = MongoTemplate(factory, converter)
        return template
    }
}