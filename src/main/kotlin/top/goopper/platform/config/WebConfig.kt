package top.goopper.platform.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.client.RestTemplate
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig : WebMvcConfigurer {

    @Value("\${cors.allowed-origins}")
    private var allowedOrigins: Array<String> = arrayOf(
        "http://localhost:3000",
        "http://xsjs.goopper.top",
        "https://xsjs.goopper.top"
    )

    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()

    @Bean
    fun restTemplate(): RestTemplate {
        return RestTemplate()
    }

    override fun addCorsMappings(registry: CorsRegistry) {
        registry
            .addMapping("/**")
            .allowedOrigins(*allowedOrigins)
            .allowedMethods("GET", "POST", "PUT", "DELETE")
            .allowCredentials(true)
            .allowedHeaders("*")
    }
}