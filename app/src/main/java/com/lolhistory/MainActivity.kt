package com.lolhistory

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.lolhistory.databinding.ActivityMainBinding
import com.lolhistory.datamodel.SummonerRankInfo
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainActivityViewModel

    private lateinit var inputMethodManager: InputMethodManager

    private var isVisibleInfoLayout = false

    private var puuid = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)

        viewModel.summonerIDInfoLiveData.observe(this) { summonerIDInfo ->
            if (summonerIDInfo == null) {
                val notExistToast = Toast.makeText(
                    applicationContext,
                    R.string.not_exist_summoner, Toast.LENGTH_SHORT
                )
                notExistToast.show()
                binding.loading.visibility = View.GONE
            } else {
                puuid = summonerIDInfo.puuid
            }
        }

        viewModel

        viewModel.summonerRankInfoLiveData.observe(this) { summonerRankInfo ->
            Log.d("TESTLOG", "summonerRankInfo: ${summonerRankInfo.tier}")
            if (summonerRankInfo != null) {
                binding.inputLayout.visibility = View.GONE
                isVisibleInfoLayout = true
                setRankInfo(summonerRankInfo)
            } else {
                binding.loading.visibility = View.GONE
            }
        }

        viewModel.matchHistoriesLiveData.observe(this) { matchHistories ->
            if (matchHistories.isEmpty()) {
                val historyErrorToast = Toast.makeText(
                    applicationContext,
                    R.string.history_error, Toast.LENGTH_SHORT
                )
                historyErrorToast.show()
            } else {
                val historyAdapter = HistoryAdapter(ArrayList(matchHistories), puuid)
                binding.rvHistory.adapter = historyAdapter
                binding.swipeLayout.isRefreshing = false
            }
            binding.loading.visibility = View.GONE
        }

        binding.rvHistory.layoutManager = LinearLayoutManager(this)
        binding.rvHistory.setHasFixedSize(true)

        binding.swipeLayout.setOnRefreshListener {
            viewModel.searchSummoner(binding.tvSummonerName.text.toString())
        }

        binding.btnInputSummoner.setOnClickListener {
            binding.loading.visibility = View.VISIBLE
            inputMethodManager.hideSoftInputFromWindow(binding.etInputSummoner.windowToken, 0)
            viewModel.searchSummoner(binding.etInputSummoner.text.toString())
            binding.etInputSummoner.setText("")
        }

        inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }

    override fun onBackPressed() {
        if (isVisibleInfoLayout) {
            // 정보 창 출력 중
            binding.infoLayout.visibility = View.GONE
            binding.inputLayout.visibility = View.VISIBLE
            isVisibleInfoLayout = !isVisibleInfoLayout
        } else {
            // 검색 창 출력 중
            finish()
        }
    }

    private fun setRankInfo(summonerRankInfo: SummonerRankInfo) {
        setTierEmblem(summonerRankInfo.tier)
        binding.tvSummonerName.text = summonerRankInfo.summonerName
        val tierRank = summonerRankInfo.tier + " " + summonerRankInfo.rank
        binding.tvTier.text = tierRank
        if (summonerRankInfo.tier == "UNRANKED") {
            // 언랭
            binding.tvRankType.text = ""
            binding.tvLp.text = ""
            binding.tvTotalWinRate.text = ""
            binding.tvTotalWinLose.text = ""
        } else {
            // 랭크 유
            binding.tvRankType.text = summonerRankInfo.queueType
            val point = summonerRankInfo.leaguePoints.toString() + "LP"
            binding.tvLp.text = point
            val rate = summonerRankInfo.wins.toDouble() / (summonerRankInfo.wins + summonerRankInfo.losses).toDouble() * 100
            binding.tvTotalWinRate.text = String.format(Locale.getDefault(), "%.2f%%", rate)
            val winAndLosses = (summonerRankInfo.wins.toString() + resources.getString(R.string.win) + " " // n승
                    + summonerRankInfo.losses.toString() + resources.getString(R.string.defeat)) // n패
            binding.tvTotalWinLose.text = winAndLosses
        }
        binding.infoLayout.visibility = View.VISIBLE
    }

    private fun setTierEmblem(tier: String) {
        when (tier) {
            "UNRANKED" -> binding.ivTierEmblem.setImageResource(R.drawable.emblem_unranked)
            "IRON" -> binding.ivTierEmblem.setImageResource(R.drawable.emblem_iron)
            "BRONZE" -> binding.ivTierEmblem.setImageResource(R.drawable.emblem_bronze)
            "SILVER" -> binding.ivTierEmblem.setImageResource(R.drawable.emblem_silver)
            "GOLD" -> binding.ivTierEmblem.setImageResource(R.drawable.emblem_gold)
            "PLATINUM" -> binding.ivTierEmblem.setImageResource(R.drawable.emblem_platinum)
            "EMERALD" -> binding.ivTierEmblem.setImageResource(R.drawable.emblem_emerald)
            "DIAMOND" -> binding.ivTierEmblem.setImageResource(R.drawable.emblem_diamond)
            "MASTER" -> binding.ivTierEmblem.setImageResource(R.drawable.emblem_master)
            "GRANDMASTER" -> binding.ivTierEmblem.setImageResource(R.drawable.emblem_grandmaster)
            "CHALLENGER" -> binding.ivTierEmblem.setImageResource(R.drawable.emblem_challenger)
        }
    }
}