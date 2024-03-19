package top.goopper.privatetemplate

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class PrivateTemplateApplication

fun main(args: Array<String>) {
    runApplication<PrivateTemplateApplication>(*args)
}
