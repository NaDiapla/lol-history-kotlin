package com.lolhistory.datamodel

import com.google.gson.annotations.SerializedName

data class SummonerIDInfo(
    @SerializedName("id")
    var id: String,

    @SerializedName("accountId")
    var accountId: String,

    @SerializedName("puuid")
    var puuid: String,

    @SerializedName("summonerLevel")
    var summonerLevel: Int
)
