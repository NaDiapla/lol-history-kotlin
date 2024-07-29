package com.lolhistory.datamodel

import com.google.gson.annotations.SerializedName

data class MatchHistory(
    @SerializedName("info")
    var info: Info
) {
    data class Info(
        @SerializedName("gameCreation")
        var gameCreation: Long,

        @SerializedName("gameDuration")
        var gameDuration: Long,

        @SerializedName("gameId")
        var gameId: Long,

        @SerializedName("queueId")
        var queueId: Int,

        @SerializedName("participants")
        var participants: ArrayList<Participants>
    ) {
        data class Participants(
            @SerializedName("assists")
            var assists: Int,

            @SerializedName("championId")
            var championId: Int,

            @SerializedName("championName")
            var championName: String,

            @SerializedName("deaths")
            var deaths: Int,

            @SerializedName("firstBloodKill")
            var firstBloodKill: Boolean,

            @SerializedName("item0")
            var item0: Int,

            @SerializedName("item1")
            var item1: Int,

            @SerializedName("item2")
            var item2: Int,

            @SerializedName("item3")
            var item3: Int,

            @SerializedName("item4")
            var item4: Int,

            @SerializedName("item5")
            var item5: Int,

            @SerializedName("item6")
            var item6: Int,

            @SerializedName("kills")
            var kills: Int,

            @SerializedName("lane")
            var lane: String,

            // 정글 미니언 처치 수
            @SerializedName("neutralMinionsKilled")
            var neutralMinionsKilled: Int,

            @SerializedName("participantId")
            var participantId: Int,

            @SerializedName("pentaKills")
            var pentaKills: Int,

            @SerializedName("puuid")
            var puuid: String,

            // 첫 번째 스펠
            @SerializedName("summoner1Id")
            var summoner1Id: Int,

            // 두 번째 스펠
            @SerializedName("summoner2Id")
            var summoner2Id: Int,

            @SerializedName("summonerId")
            var summonerId: String,

            @SerializedName("summonerName")
            var summonerName: String,

            @SerializedName("teamPosition")
            var teamPosition: String,

            // 미니언 처치 수(정글 몹 제외)
            @SerializedName("totalMinionsKilled")
            var totalMinionsKilled: Int,

            @SerializedName("win")
            var win: Boolean,

            @SerializedName("perks")
            var perks: Perks
        ) {
            data class Perks(
                @SerializedName("styles")
                var styles: ArrayList<Styles>
            ) {
                data class Styles(
                    @SerializedName("description")
                    var description: String,

                    @SerializedName("selections")
                    var selections: ArrayList<Selections>,

                    // 룬 범주(정복, 지배, 마법, 결의, 영감)
                    @SerializedName("style")
                    var style: Int
                ) {
                    data class Selections(
                        // 룬 상세(정복자, 치명적 속도, 마나 순환 팔찌 등..)
                        @SerializedName("perk")
                        var perk: Int
                    )
                }
            }
        }
    }
}