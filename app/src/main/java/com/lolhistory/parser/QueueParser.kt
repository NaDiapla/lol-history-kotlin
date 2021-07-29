package com.lolhistory.parser

import android.content.Context
import android.util.Log
import org.json.JSONArray
import java.nio.charset.StandardCharsets

class QueueParser(private val context: Context) {

    fun getQueueType(queueId: Int): String {
        var queueDescription = "";
        try {
            val inputStream = context.assets.open("queues.json")
            val buffer = ByteArray(inputStream.available())
            inputStream.read(buffer)
            inputStream.close()
            val json = String(buffer, StandardCharsets.UTF_8)

            val jsonArray = JSONArray(json)
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                if (jsonObject["queueId"] == queueId) {
                    queueDescription = jsonObject.getString("description")
                    break
                }
            }
        } catch (e: Exception) {
            Log.d("TESTLOG", "[getQueueType] exception: $e")
            e.printStackTrace()
        }

        return queueDescription
    }
}