package top.goopper.platform.controller

import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import top.goopper.platform.pojo.Response
import top.goopper.platform.service.FileService

@RestController
@RequestMapping("/file")
class FileController(
    private val fileService: FileService
) {

    private val logger = LoggerFactory.getLogger(FileController::class.java)

    @PostMapping("/upload")
    fun upload(@RequestParam upload: MultipartFile, request: HttpServletRequest): ResponseEntity<Response> {
        val realIp = request.getHeader("X-Forwarded-For")
        logger.info("File upload, ip: $realIp, size: ${upload.size}")
        val result = fileService.upload(upload)
        return ResponseEntity.ok(Response.success(result))
    }

    @DeleteMapping("/delete")
    fun delete(@RequestParam filename: String): ResponseEntity<Response> {
        fileService.delete(filename)
        return ResponseEntity.ok(Response.success())
    }

}