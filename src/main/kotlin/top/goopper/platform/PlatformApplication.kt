package top.goopper.platform

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.transaction.annotation.EnableTransactionManagement

@SpringBootApplication
@EnableDiscoveryClient
@EnableMethodSecurity
@EnableTransactionManagement
class PlatformApplication

fun main(args: Array<String>) {
    runApplication<PlatformApplication>(*args)
}