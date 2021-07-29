package com.lolhistory.datamodel

import com.google.gson.annotations.SerializedName

data class SummonerRankInfo(
    /**
     * summonerName: 소환사 이름
     * queueType: 솔랭/자랭 랭크 타입
     * tier: 티어(챌린저 ~ 언랭)
     * rank: 랭크 (I ~ IV)
     * leaguePoints: LP
     * wins: 승리 수
     * losses: 패배 수
     */
    @SerializedName("summonerName")
    var summonerName: String,

    @SerializedName("queueType")
    var queueType: String,

    @SerializedName("tier")
    var tier: String,

    @SerializedName("rank")
    var rank: String,

    @SerializedName("leaguePoints")
    var leaguePoints: Int,

    @SerializedName("wins")
    var wins: Int,

    @SerializedName("losses")
    var losses: Int
)