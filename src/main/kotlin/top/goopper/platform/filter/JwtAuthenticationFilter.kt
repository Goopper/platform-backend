package top.goopper.platform.filter

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.util.AntPathMatcher
import org.springframework.web.filter.OncePerRequestFilter
import top.goopper.platform.service.JwtTokenService
import top.goopper.platform.config.SecurityConfig.Companion.AUTHORIZATION_HEADER
import top.goopper.platform.config.SecurityConfig.Companion.whiteList

class JwtAuthenticationFilter(private val jwtTokenService: JwtTokenService) : OncePerRequestFilter() {

    private val authLogger = LoggerFactory.getLogger(JwtAuthenticationFilter::class.java)
    private val pathMatcher = AntPathMatcher()

    /**
     * 重写shouldNotFilter方法，返回true表示不进行过滤
     * @param request 请求
     * @return 是否过滤
     */
    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        return whiteList.any { pathMatcher.match(it, request.requestURI) }
    }

    /**
     * 重写doFilterInternal方法，进行token验证
     * @throws Exception Token验证异常（过期，不存在，异常）
     */
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val token = request.getHeader(AUTHORIZATION_HEADER)
        // verify token
        try {
            val payload = jwtTokenService.validateAuthToken(token)
            // if token is valid, set authentication
            val authentication = jwtTokenService.getAuthentication(payload)
            SecurityContextHolder.getContext().authentication = authentication
            // if token is valid, renew token expiration
            jwtTokenService.renewAuthTokenExpiration(token)
        } catch (e: Exception) {
            authLogger.error("Token verify failed: ${e.message}")
        }
        filterChain.doFilter(request, response)
    }
}