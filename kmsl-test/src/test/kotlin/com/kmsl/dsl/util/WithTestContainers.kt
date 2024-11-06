package com.kmsl.dsl.util

import com.github.dockerjava.api.model.ExposedPort
import com.github.dockerjava.api.model.PortBinding
import com.github.dockerjava.api.model.Ports.Binding.bindPort
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.MongoDBContainer

internal interface WithTestContainers {
    companion object {
        @JvmStatic
        @DynamicPropertySource
        fun initTestContainers(
            registry: DynamicPropertyRegistry,
        ) {
            activeTestContainers.parallelStream().forEach { it.start() }
        }

        private val mongoDB by lazy {
            MongoDBContainer("mongo").apply {
                withCreateContainerCmdModifier {
                    it.withName("mongo-test-container")
                        .hostConfig
                        ?.portBindings
                        ?.add(
                            PortBinding(
                                bindPort(27017),
                                ExposedPort(27017),
                            )
                        )

                }
            }
        }

        private val activeTestContainers = listOf(mongoDB)
    }
}