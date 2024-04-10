package top.goopper.platform.enum

enum class CourseStatusEnum(val id: Int, val desc: String) {
    DRAFT(1, "草稿"),
    USING(2, "使用中"),
    DEACTIVATED(3, "已停用"),
}