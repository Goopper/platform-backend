package top.goopper.platform.advice

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import top.goopper.platform.pojo.Response

@ControllerAdvice
class GlobalExceptionAdvice {

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<Response> {
        // print for debug
        e.printStackTrace()
        return ResponseEntity.status(500).body(Response.error(500, e.message ?: "Unknown error"))
    }

}