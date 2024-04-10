package top.goopper.platform.enum

enum class MessageTypeEnum(val id: Int, val value: String) {

    REQUIRE_CORRECT(1, "作业批改"),
    CORRECTED(2, "作业批改完成"),
    NOTICE(2, "通知"),
    SYSTEM(3, "系统消息"),

}