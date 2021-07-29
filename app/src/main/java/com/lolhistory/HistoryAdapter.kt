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

class HistoryAdapter(private var matchHistories: ArrayList<MatchHistory>, private val puuid: String): RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

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

            Glide.with(context).load(getChampionPortraitURL(matchHistory, playerIndex)).into(binding.ivChampionPortrait)

            Glide.with(context).load(getSpellImageURL(matchHistory, playerIndex, 1)).into(binding.ivSummonerSpell1)
            Glide.with(context).load(getSpellImageURL(matchHistory, playerIndex, 2)).into(binding.ivSummonerSpell2)

            Glide.with(context).load(getRuneImageURL(matchHistory, playerIndex, 1)).into(binding.ivKeystoneRune)
            Glide.with(context).load(getRuneImageURL(matchHistory, playerIndex, 2)).into(binding.ivSecondaryRune)

            binding.tvKda.text = getKDA(matchHistory, playerIndex)

            val itemArray = arrayOf(binding.ivItem0, binding.ivItem1, binding.ivItem2, binding.ivItem3, binding.ivItem4, binding.ivItem5, binding.ivItem6)

            for (i in itemArray.indices) {
                val itemUrl = getItemImageURL(matchHistory, playerIndex, i)
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
        val playerIndex = getPlayerIndex(matchHistory)

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

    private fun getDurationTime(millisecondTime: Long): String {
        val secondTime = millisecondTime / 1000
        val min = secondTime / 60
        val second = secondTime % 60

        return (String.format(Locale.getDefault(), "%02d", min) + ":"
                + String.format(Locale.getDefault(), "%02d", second))
    }

    private fun getQueueType(queueId: Int): String {
        val parser = QueueParser(context)
        return parser.getQueueType(queueId)
    }

    private fun getChampionPortraitURL(matchHistory: MatchHistory, playerIndex: Int): String {
        val championName = matchHistory.info.participants[playerIndex].championName
        return BaseUrl.RIOT_DATA_DRAGON_GET_CHAMPION_PORTRAIT + "$championName.png"
    }

    private fun getSpellImageURL(matchHistory: MatchHistory, playerIndex: Int, spellIndex: Int): String {
        var spellId = 0
        if (spellIndex == 1) {
            // 첫번째 스펠
            spellId = matchHistory.info.participants[playerIndex].summoner1Id
        } else if (spellIndex == 2) {
            // 두번째 스펠
            spellId = matchHistory.info.participants[playerIndex].summoner2Id
        }

        val parser = SpellParser(context)
        val spellName = parser.getSpellName(spellId)

        return BaseUrl.RIOT_DATA_DRAGON_GET_SPELL_IMAGE + "$spellName.png"
    }

    private fun getRuneImageURL(matchHistory: MatchHistory, playerIndex: Int, runeIndex: Int): String {
        var runeId = 0
        for (style in matchHistory.info.participants[playerIndex].perks.styles) {
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

    private fun getKDA(matchHistory: MatchHistory, playerIndex: Int): String? {
        val kills = matchHistory.info.participants[playerIndex].kills
        val deaths = matchHistory.info.participants[playerIndex].deaths
        val assists = matchHistory.info.participants[playerIndex].assists
        return "$kills / $deaths / $assists"
    }

    private fun getItemImageURL(matchHistory: MatchHistory, playerIndex: Int, itemIndex: Int): String {
        var itemId = 0
        when (itemIndex) {
            0 -> itemId = matchHistory.info.participants[playerIndex].item0
            1 -> itemId = matchHistory.info.participants[playerIndex].item1
            2 -> itemId = matchHistory.info.participants[playerIndex].item2
            3 -> itemId = matchHistory.info.participants[playerIndex].item3
            4 -> itemId = matchHistory.info.participants[playerIndex].item4
            5 -> itemId = matchHistory.info.participants[playerIndex].item5
            6 -> itemId = matchHistory.info.participants[playerIndex].item6
        }
        return if (itemId != 0) {
            BaseUrl.RIOT_DATA_DRAGON_GET_ITEM_IMAGE + "$itemId.png"
        } else {
            ""
        }
    }

}