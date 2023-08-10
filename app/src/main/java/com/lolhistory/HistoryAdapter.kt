package com.lolhistory

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lolhistory.databinding.MatchHistoryItemBinding
import com.lolhistory.datamodel.MatchHistory
import com.lolhistory.parser.QueueParser
import com.lolhistory.parser.RuneParser
import com.lolhistory.parser.SpellParser
import com.lolhistory.retrofit.BaseUrl
import java.util.*

class HistoryAdapter(
    private var matchHistories: ArrayList<MatchHistory>,
    private val puuid: String
): RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    private lateinit var context: Context

    inner class ViewHolder(private val binding: MatchHistoryItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(matchHistory: MatchHistory, playerIndex: Int) {
            if (matchHistory.info.participants[playerIndex].win) {
                binding.resultLayout.setBackgroundColor(context.getColor(R.color.colorWin))
                binding.tvResult.setText(R.string.win)
            } else {
                binding.resultLayout.setBackgroundColor(context.getColor(R.color.colorDefeat))
                binding.tvResult.setText(R.string.defeat)
            }
            binding.tvDurationTime.text = getDurationTime(matchHistory.info.gameDuration)

            binding.tvGameType.text = getQueueType(matchHistory.info.queueId)

            Glide.with(context)
                .load(getChampionPortraitURL(matchHistory.info.participants[playerIndex]))
                .into(binding.ivChampionPortrait)

            Glide.with(context)
                .load(getSpellImageURL(matchHistory.info.participants[playerIndex], 1))
                .into(binding.ivSummonerSpell1)
            Glide.with(context)
                .load(getSpellImageURL(matchHistory.info.participants[playerIndex], 2))
                .into(binding.ivSummonerSpell2)

            Glide.with(context)
                .load(getRuneImageURL(matchHistory.info.participants[playerIndex], 1))
                .into(binding.ivKeystoneRune)
            Glide.with(context)
                .load(getRuneImageURL(matchHistory.info.participants[playerIndex], 2))
                .into(binding.ivSecondaryRune)

            binding.tvKda.text = getKDA(matchHistory.info.participants[playerIndex])

            val itemArray = arrayOf(binding.ivItem0, binding.ivItem1, binding.ivItem2, binding.ivItem3, binding.ivItem4, binding.ivItem5, binding.ivItem6)

            for (i in itemArray.indices) {
                val itemUrl = getItemImageURL(matchHistory.info.participants[playerIndex], i)
                if (itemUrl.isNotEmpty())
                    Glide.with(context).load(itemUrl).into(itemArray[i])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryAdapter.ViewHolder {
        context = parent.context

        val binding = MatchHistoryItemBinding.inflate(LayoutInflater.from(context), parent, false)
        matchHistories.sortByDescending { it.info.gameCreation }
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val matchHistory = matchHistories[position]
        holder.bind(matchHistory, getPlayerIndex(matchHistory))
    }

    override fun getItemCount(): Int {
        return matchHistories.size
    }

    private fun getPlayerIndex(matchHistory: MatchHistory): Int {
        var i = 0
        for (participants in matchHistory.info.participants) {
            Log.d("TESTLOG", "[getPlayerIndex] getAccountId: " + participants.puuid)
            if (participants.puuid == puuid) break
            else i++
        }
        return i
    }

    private fun getDurationTime(secondTime: Long): String {
        val min = secondTime / 60
        val second = secondTime % 60

        return (String.format(Locale.getDefault(), "%02d", min) + ":"
                + String.format(Locale.getDefault(), "%02d", second))
    }

    private fun getQueueType(queueId: Int): String {
        val parser = QueueParser(context)
        return parser.getQueueType(queueId)
    }

    private fun getChampionPortraitURL(participants: MatchHistory.Info.Participants): String {
        val championName = participants.championName
        return BaseUrl.RIOT_DATA_DRAGON_GET_CHAMPION_PORTRAIT + "$championName.png"
    }

    private fun getSpellImageURL(participants: MatchHistory.Info.Participants, spellIndex: Int): String {
        var spellId = 0
        if (spellIndex == 1) {
            // 첫번째 스펠
            spellId = participants.summoner1Id
        } else if (spellIndex == 2) {
            // 두번째 스펠
            spellId = participants.summoner2Id
        }

        val parser = SpellParser(context)
        val spellName = parser.getSpellName(spellId)

        return BaseUrl.RIOT_DATA_DRAGON_GET_SPELL_IMAGE + "$spellName.png"
    }

    private fun getRuneImageURL(participants: MatchHistory.Info.Participants, runeIndex: Int): String {
        var runeId = 0
        for (style in participants.perks.styles) {
            if (runeIndex == 1) {
                if (style.description == "primaryStyle") runeId = style.selections[0].perk
            } else if (runeIndex == 2) {
                if (style.description == "subStyle") runeId = style.style
            }
        }

        val parser = RuneParser(context)
        val icon: String = parser.getRuneIcon(runeId)
        return BaseUrl.RIOT_DATA_DRAGON_GET_RUNE_IMAGE + icon
    }

    private fun getKDA(participants: MatchHistory.Info.Participants): String {
        val kills = participants.kills
        val deaths = participants.deaths
        val assists = participants.assists
        return "$kills / $deaths / $assists"
    }

    private fun getItemImageURL(participants: MatchHistory.Info.Participants, itemIndex: Int): String {
        var itemId = 0
        when (itemIndex) {
            0 -> itemId = participants.item0
            1 -> itemId = participants.item1
            2 -> itemId = participants.item2
            3 -> itemId = participants.item3
            4 -> itemId = participants.item4
            5 -> itemId = participants.item5
            6 -> itemId = participants.item6
        }
        return if (itemId != 0) {
            BaseUrl.RIOT_DATA_DRAGON_GET_ITEM_IMAGE + "$itemId.png"
        } else {
            ""
        }
    }

}