package dev.suresh

import io.micronaut.http.annotation.*
import io.micronaut.http.annotation.Controller
import org.slf4j.*

@Controller
class Controller {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @Get("/1")
    fun test(): String {
        logger.info("OK..Kotlin")
        return "Hello Kotlin!"
    }

    @Get("/2")
    fun test2() {

    }

    @Get("/3")
    fun test1() {

    }
}