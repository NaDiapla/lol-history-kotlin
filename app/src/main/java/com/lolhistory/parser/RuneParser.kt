package com.lolhistory.parser

import android.content.Context
import android.util.Log
import org.json.JSONArray
import java.nio.charset.StandardCharsets

class RuneParser(private val context: Context) {

    fun getRuneIcon(runeId: Int): String {
        // 서브 룬 범주는 100 단위로 끊어지기 때문에
        val isSubRune = runeId % 100 == 0
        var icon = ""
        try {
            val inputStream = context.assets.open("runes_reforged.json")
            val buffer = ByteArray(inputStream.available())
            inputStream.read(buffer)
            inputStream.close()

            val json = String(buffer, StandardCharsets.UTF_8)
            val jsonArray = JSONArray(json)

            for (i in 0 until jsonArray.length()) {
                val mainObject = jsonArray.getJSONObject(i)
                if (isSubRune) {
                    // 서브 룬 범주 아이콘(정밀, 지배, 마법, 결의, 영감)
                    if (mainObject["id"] == runeId) {
                        icon = mainObject.getString("icon")
                        break
                    }
                } else {
                    // 메인 룬 아이콘(정복자, 감전 등..)
                    val slots = mainObject.getString("slots")
                    val slotsArray = JSONArray(slots)
                    val slotObject = slotsArray.getJSONObject(0)
                    val runes = slotObject.getString("runes")
                    val runesArray = JSONArray(runes)
                    for (j in 0 until runesArray.length()) {
                        val runeObject = runesArray.getJSONObject(j)
                        if (runeObject["id"] == runeId) {
                            icon = runeObject.getString("icon")
                            break
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.d("TESTLOG", "[getRuneIcon] exception: $e")
            e.printStackTrace()
        }
        return icon
    }
}