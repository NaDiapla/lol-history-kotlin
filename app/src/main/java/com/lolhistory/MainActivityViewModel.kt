package com.lolhistory

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lolhistory.datamodel.MatchHistory
import com.lolhistory.datamodel.MatchList
import com.lolhistory.datamodel.SummonerIDInfo
import com.lolhistory.datamodel.SummonerRankInfo
import com.lolhistory.repository.RiotRepository
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable

class MainActivityViewModel: ViewModel() {
    private val repo = RiotRepository()

    private val summonerIDInfoLiveData = MutableLiveData<SummonerIDInfo>()
    private val summonerRankInfoLiveData = MutableLiveData<SummonerRankInfo>()
    private val historyAdapterLiveData = MutableLiveData<HistoryAdapter>()

    private var summonerName = ""

    private var matchHistories: ArrayList<MatchHistory> = ArrayList()

    fun getSummonerIDInfoLiveData(): MutableLiveData<SummonerIDInfo> {
        return summonerIDInfoLiveData
    }

    fun getSummonerRankInfoLiveData(): MutableLiveData<SummonerRankInfo> {
        return summonerRankInfoLiveData
    }

    fun getHistoryAdapterLiveData(): MutableLiveData<HistoryAdapter> {
        return historyAdapterLiveData
    }

    fun searchSummoner(name: String) {
        this.summonerName = name

        matchHistories.clear()

        repo.getSummonerIdInfo(name).subscribe(object :SingleObserver<SummonerIDInfo> {
            override fun onSubscribe(d: Disposable) {
            }

            override fun onSuccess(summonerIDInfo: SummonerIDInfo) {
                summonerName = summonerIDInfo.name
                getSummonerRankInfo(summonerIDInfo.id)
                getMatchList(summonerIDInfo.puuid)
                Log.d("TESTLOG", "[getSummonerIdInfo] id: " + summonerIDInfo.puuid)
            }

            override fun onError(e: Throwable) {
                Log.d("TESTLOG", "[getSummonerIdInfo] exception: $e")
                summonerIDInfoLiveData.value = null
            }
        })
    }

    private fun getSummonerRankInfo(summonerId: String) {
        repo.getSummonerRankInfo(summonerId).subscribe(object: SingleObserver<List<SummonerRankInfo>> {
            override fun onSubscribe(d: Disposable) {
            }

            override fun onSuccess(summonerRankInfos: List<SummonerRankInfo>) {
                setSummonerRankInfo(summonerRankInfos)
            }

            override fun onError(e: Throwable) {
                Log.d("TESTLOG", "[getSummonerRankInfo] exception: $e")
            }
        })
    }

    private fun getMatchList(puuid: String) {
        repo.getMatchHistoryList(puuid).subscribe(object: SingleObserver<ArrayList<String>> {
            override fun onSubscribe(d: Disposable) {
            }

            override fun onSuccess(array: ArrayList<String>) {
                var count = 0
                for (match in array) {
                    // Riot Development Key: 20 requests every 1 seconds(s)
                    if (count < 15) {
                        getMatchHistory(match, puuid)
                        count++
                    } else {
                        break
                    }
                }
            }

            override fun onError(e: Throwable) {
                Log.d("TESTLOG", "[getMatchList] exception: $e")
            }
        })
    }

    private fun getMatchHistory(matchId: String, puuid: String) {
        repo.getMatchHistory(matchId).subscribe(object: SingleObserver<MatchHistory> {
            override fun onSubscribe(d: Disposable) {
            }

            override fun onSuccess(matchHistory: MatchHistory) {
                matchHistories.add(matchHistory)
                if (matchHistories.size > 14) {
                    // RecyclerView Adapter MainActivity에 전달
                    val historyAdapter = HistoryAdapter(matchHistories, puuid)
                    historyAdapterLiveData.value = historyAdapter
                }
            }

            override fun onError(e: Throwable) {
                Log.d("TESTLOG", "[getMatchHistory] exception: $e")
                historyAdapterLiveData.value = null

            }
        })
    }

    private fun setSummonerRankInfo(summonerRankInfos: List<SummonerRankInfo>) {
        var soloRankInfo: SummonerRankInfo? = null
        var flexRankInfo: SummonerRankInfo? = null
        var soloRankTier = 0
        var flexRankTier = 0
        if (summonerRankInfos.isEmpty()) {
            // 언랭
            val unRankInfo = SummonerRankInfo(summonerName, "", "UNRANKED", "", 0, 0, 0)
            summonerRankInfoLiveData.setValue(unRankInfo)
        } else {
            for (info in summonerRankInfos) {
                if (info.queueType == "RANKED_SOLO_5x5") {
                    // 솔랭
                    soloRankInfo = info
                    soloRankTier = calcTier(info.tier, info.rank, info.leaguePoints)
                } else if (info.queueType == "RANKED_FLEX_SR") {
                    // 자랭
                    flexRankInfo = info
                    flexRankTier = calcTier(info.tier, info.rank, info.leaguePoints)
                }
                if (soloRankTier < flexRankTier) {
                    // 자랭 티어가 더 높을 때
                    summonerRankInfoLiveData.setValue(flexRankInfo)
                } else {
                    // 솔랭 티어가 더 높거나 솔랭과 자랭의 티어가 같을 때
                    summonerRankInfoLiveData.setValue(soloRankInfo)
                }
            }
        }
    }

    private fun calcTier(tier: String, rank: String, lp: Int): Int {
        var tierNum = 0
        when (tier) {
            "IRON" -> { }
            "BRONZE" -> tierNum = 1000
            "SILVER" -> tierNum = 2000
            "GOLD" -> tierNum = 3000
            "PLATINUM" -> tierNum = 4000
            "DIAMOND" -> tierNum = 5000
            "MASTER" -> tierNum = 6000
            "GRANDMASTER" -> tierNum = 7000
            "CHALLENGER" -> tierNum = 8000
        }
        when (rank) {
            "I" -> tierNum += 700
            "II" -> tierNum += 500
            "III" -> tierNum += 300
            "IV" -> tierNum += 100
        }
        tierNum += lp
        return tierNum
    }
}