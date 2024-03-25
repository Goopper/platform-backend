package top.goopper.platform.advice

import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import top.goopper.platform.pojo.Response

@ControllerAdvice
class GlobalExceptionAdvice {

    @Value("\${spring.profiles.active}")
    private lateinit var profile: String

    private val logger = LoggerFactory.getLogger(GlobalExceptionAdvice::class.java)

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception, request: HttpServletRequest): ResponseEntity<Response> {
        // print for debug
        if (profile == "dev") {
            e.printStackTrace()
        }
        logger.error("Global exception: ip: ${request.remoteAddr}, url: ${request.requestURL}, message: ${e.message}")
        return ResponseEntity.status(500).body(Response.error(500, e.message ?: "Unknown error"))
    }

}