package com.lolhistory.parser

import android.content.Context
import android.util.Log
import org.json.JSONObject
import java.nio.charset.StandardCharsets

class SpellParser(private val context: Context) {

    fun getSpellName(spellKey: Int): String {
        var spellName: String = ""
        try {
            val inputStream = context.assets.open("summoner.json")
            val buffer = ByteArray(inputStream.available())
            inputStream.read(buffer)
            inputStream.close()

            val json = String(buffer, StandardCharsets.UTF_8)
            val jsonObject = JSONObject(json)

            val dataValue = jsonObject.getString("data")
            val dataObject = JSONObject(dataValue)
            val iterator: Iterator<*> = dataObject.keys()

            while (iterator.hasNext()) {
                val name = iterator.next().toString()
                val spellValue = dataObject.getString(name)
                val spellObject = JSONObject(spellValue)
                if (spellKey.toString() == spellObject.getString("key")) {
                    spellName = name
                    break
                }
            }
        } catch (e: Exception) {
            Log.d("TESTLOG", "[getSpellName] exception: $e")
            e.printStackTrace()
        }
        return spellName
    }
}