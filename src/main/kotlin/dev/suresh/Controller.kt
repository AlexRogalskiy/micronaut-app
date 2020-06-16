package dev.suresh

import io.micronaut.http.annotation.*
import io.micronaut.http.annotation.Controller
import org.slf4j.*

@Controller
class Controller {


    private val logger = LoggerFactory.getLogger(this::class.java)

    @Get("/abc")
    fun test(): String {
        logger.info("OK..Kotlin")
        return "Hello Kotlin."
    }

    @Get("/abc2")
    fun test2() {

    }

    @Get("/abc1")
    fun test1() {

    }
}