package top.goopper.platform

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.cloud.context.config.annotation.RefreshScope

@SpringBootApplication
@EnableDiscoveryClient
class PlatformApplication

fun main(args: Array<String>) {
    runApplication<PlatformApplication>(*args)
}