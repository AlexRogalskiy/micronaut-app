package dev.suresh

import io.micronaut.runtime.*
import io.micronaut.test.annotation.*
import org.junit.jupiter.api.*
import javax.inject.*

@MicronautTest
class MicronautAppTest {

    @Inject
    lateinit var application: EmbeddedApplication<*>

    @Test
    fun testItWorks() {
        Assertions.assertTrue(application.isRunning)
    }
}