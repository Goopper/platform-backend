package top.goopper.platform

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient

@SpringBootApplication
@EnableDiscoveryClient
class PlatformApplication

fun main(args: Array<String>) {
    runApplication<PlatformApplication>(*args)
}