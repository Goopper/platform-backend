package top.goopper.platform.controller.course

import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
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
        @PathVariable courseId: Int
    ): ResponseEntity<Response> {
        section.courseId = courseId
        sectionService.createNewSection(section)
        return ResponseEntity.ok(Response.success())
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @GetMapping("/creation/{sectionId}")
    fun getCreationInfo(
        @PathVariable sectionId: Int
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
        @PathVariable sectionId: Int
    ): ResponseEntity<Response> {
        sectionService.deleteSection(sectionId)
        return ResponseEntity.ok(Response.success())
    }

    /**
     * 获取章节详细信息
     */
    @GetMapping("/{sectionId}")
    fun getSectionDetail(
        @PathVariable sectionId: Int
    ): ResponseEntity<Response> {
        val section = sectionService.getSectionDetail(sectionId)
        return ResponseEntity.ok(Response.success(section))
    }

}