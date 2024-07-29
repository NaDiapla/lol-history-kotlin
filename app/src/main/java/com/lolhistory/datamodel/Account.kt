package com.lolhistory.datamodel

import com.google.gson.annotations.SerializedName

data class Account(
    @SerializedName("puuid")
    val puuid: String,

    @SerializedName("gameName")
    val gameName: String,

    @SerializedName("tagLine")
    val tagLine: String
)