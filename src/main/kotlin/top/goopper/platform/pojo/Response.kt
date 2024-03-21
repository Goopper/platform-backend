package top.goopper.platform.pojo

data class Response(
    var code: Int,
    var message: String,
    var data: Any?
) {
    companion object {
        fun success(data: Any?=null): Response {
            return Response(200, "success", data)
        }

        fun error(code: Int, message: String): Response {
            return Response(code, message, null)
        }
    }
}