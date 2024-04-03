package top.goopper.platform.controller.course

import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import top.goopper.platform.dto.course.create.CreateSectionDTO
import top.goopper.platform.pojo.Response
import top.goopper.platform.service.course.SectionService

@RestController
@RequestMapping("/section")
class SectionController(
    private val sectionService: SectionService
) {

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @PostMapping("/{courseId}")
    fun createNewSection(
        @RequestBody section: CreateSectionDTO,
        @PathVariable courseId: Long
    ): ResponseEntity<Response> {
        section.courseId = courseId
        sectionService.createNewSection(section)
        return ResponseEntity.ok(Response.success())
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @GetMapping("/creation/{sectionId}")
    fun getCreationInfo(
        @PathVariable sectionId: Long
    ): ResponseEntity<Response> {
        val creation = sectionService.getCreationInfo(sectionId)
        return ResponseEntity.ok(Response.success(creation))
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @PutMapping
    fun updateSection(
        @RequestBody section: CreateSectionDTO,
    ): ResponseEntity<Response> {
        sectionService.updateSection(section)
        return ResponseEntity.ok(Response.success())
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @DeleteMapping("/{sectionId}")
    fun deleteSection(
        @PathVariable sectionId: Long
    ): ResponseEntity<Response> {
        sectionService.deleteSection(sectionId)
        return ResponseEntity.ok(Response.success())
    }

    @GetMapping("/{sectionId}")
    fun getSectionDetail(
        @PathVariable sectionId: Long
    ): ResponseEntity<Response> {
        val section = sectionService.getSectionDetail(sectionId)
        return ResponseEntity.ok(Response.success(section))
    }

}