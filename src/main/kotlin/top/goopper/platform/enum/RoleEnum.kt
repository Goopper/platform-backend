package top.goopper.platform.enum

enum class RoleEnum(val value: String, val id: Int) {
    STUDENT("ROLE_STUDENT", 3),
    TEACHER("ROLE_TEACHER", 2),
    ADMIN("ROLE_ADMIN", 1)
}