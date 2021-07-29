package com.lolhistory.datamodel

import com.google.gson.annotations.SerializedName

data class MatchList_old(
    @SerializedName("matches")
    val matches: ArrayList<Match>
    ) {
    data class Match(
        @SerializedName("gameId")
        val gameId: String,

        @SerializedName("champion")
        val champion: String,

        @SerializedName("queue")
        val queue: String
    )
}