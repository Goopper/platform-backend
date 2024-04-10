package top.goopper.platform.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import top.goopper.platform.dto.message.MessageQueryDTO
import top.goopper.platform.pojo.Response
import top.goopper.platform.service.MessageService

@RestController
@RequestMapping("/message")
class MessageController(
    private val messageService: MessageService
) {

    @GetMapping
    fun messages(
        @RequestParam page: Int = 1,
        @RequestParam title: String = "",
        @RequestParam typeId: Int = -1
    ): ResponseEntity<Response> {
        val dto = MessageQueryDTO(page, title, typeId)
        val messages = messageService.getPage(dto)
        return ResponseEntity.ok(Response.success(messages))
    }

    @GetMapping("/receive/{messageId}")
    fun receiveOne(
        @PathVariable messageId: Int
    ): ResponseEntity<Response> {
        messageService.receiveOne(messageId)
        return ResponseEntity.ok(Response.success())
    }

}