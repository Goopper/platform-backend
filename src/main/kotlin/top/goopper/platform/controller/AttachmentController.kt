package top.goopper.platform.controller

import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import top.goopper.platform.pojo.Response
import top.goopper.platform.service.AttachmentService

@RestController
@RequestMapping("/attachment")
class AttachmentController(
    private val attachmentService: AttachmentService
) {

    private val logger = LoggerFactory.getLogger(AttachmentController::class.java)

    // upload file to s3
    // TODO content conflict check
    @PostMapping("/upload")
    fun upload(@RequestParam upload: MultipartFile, request: HttpServletRequest): ResponseEntity<Response> {
        val realIp = request.getHeader("X-Forwarded-For")
        logger.info("File upload, ip: $realIp, size: ${upload.size}")
        val result = attachmentService.uploadAttachment(upload)
        return ResponseEntity.ok(Response.success(result))
    }

    @DeleteMapping("/delete")
    fun delete(@RequestParam filename: String): ResponseEntity<Response> {
        attachmentService.delete(filename)
        return ResponseEntity.ok(Response.success())
    }

}