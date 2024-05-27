package top.goopper.platform.config

import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import top.goopper.platform.filter.JwtAuthenticationFilter
import top.goopper.platform.service.JwtTokenService
import top.goopper.platform.service.UserService

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val userService: UserService,
    private val passwordEncoder: BCryptPasswordEncoder
) {
    companion object {
        var whiteList = arrayOf(
            "/login",
            "/oauth/*/url",
            "/oauth/*/auth",
            "/oauth/login/*",
            "/health",
            "/statistic/baize/**",
            )
        const val AUTHORIZATION_HEADER = "G-Authorization"
    }

    @Value("\${security.white-list}")
    private lateinit var configWhiteList: Array<String>

    @PostConstruct
    fun init() {
        whiteList = configWhiteList
    }

    @Bean
    fun providerManager() = ProviderManager(
        DaoAuthenticationProvider().apply {
            setUserDetailsService(userService)
            setPasswordEncoder(passwordEncoder)
        }
    )

    @Bean
    fun filterChain(http: HttpSecurity, jwtProvider: JwtTokenService): SecurityFilterChain = http
        .authorizeHttpRequests {
            it.requestMatchers(*whiteList).permitAll()
                .anyRequest().authenticated()
        }
        .formLogin { it.disable() }
        .httpBasic { it.disable() }
        .logout { it.disable() }
        .csrf { it.disable() }
        .authenticationManager(providerManager())
        .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
        .addFilterBefore(JwtAuthenticationFilter(jwtProvider), UsernamePasswordAuthenticationFilter::class.java)
        .build()
}