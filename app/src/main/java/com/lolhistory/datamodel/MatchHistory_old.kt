package com.lolhistory.datamodel

import com.google.gson.annotations.SerializedName

data class MatchHistory_old(
    @SerializedName("gameId")
    val gameId: String,

    @SerializedName("queueId")
    val queueId: Int,

    @SerializedName("gameCreation")
    val gameCreation: Long,

    @SerializedName("gameDuration")
    val gameDuration: Long,

    @SerializedName("participants")
    val participants: ArrayList<Participants>,

    @SerializedName("participantIdentities")
    val participantIdentities: ArrayList<ParticipantIdentities>
    ) {
    data class Participants(
        @SerializedName("championId")
        private val championId: Int,

        @SerializedName("spell1Id")
        private val spell1Id: Int,

        @SerializedName("spell2Id")
        private val spell2Id: Int,

        @SerializedName("stats")
        private val stats: Stats
    ) {
        data class Stats(
            @SerializedName("win")
            private val win: Boolean,

            @SerializedName("item0")
            private val item0: Int,

            @SerializedName("item1")
            private val item1: Int,

            @SerializedName("item2")
            private val item2: Int,

            @SerializedName("item3")
            private val item3: Int,

            @SerializedName("item4")
            private val item4: Int,

            @SerializedName("item5")
            private val item5: Int,

            @SerializedName("item6")
            private val item6: Int,

            @SerializedName("kills")
            private val kills: Int,

            @SerializedName("deaths")
            private val deaths: Int,

            @SerializedName("assists")
            private val assists: Int,

            @SerializedName("perk0")
            private val perk0: Int,

            @SerializedName("perkSubStyle")
            private val perkSubStyle: Int,
        )
    }
    data class ParticipantIdentities(
        @SerializedName("participantId")
        val participantId: Int,

        @SerializedName("player")
        val player: Player
    ) {
        data class Player(
            @SerializedName("accountId")
            val accountId: String,

            @SerializedName("summonerName")
            val summonerName: String
        )
    }
}