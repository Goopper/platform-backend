package top.goopper.platform.utils

import org.springframework.stereotype.Component

@Component
class RedisUtils {

    val LATEST_LEARNED_SECTION = "section".toByteArray()
    val LATEST_LEARNED_TASK = "task".toByteArray()
    val LATEST_LEARNED_DATE = "date".toByteArray()

    fun buildLatestLearnedKey(uid: Int, cid: Int): ByteArray = "learn:$uid:current:$cid".toByteArray()

    fun buildLearnedYearsListKey(uid: Int): ByteArray = "learn:$uid:year:all".toByteArray()

    fun buildYearLearnedKey(uid: Int, currentYear: Int): ByteArray = "learn:$uid:year:$currentYear".toByteArray()

}