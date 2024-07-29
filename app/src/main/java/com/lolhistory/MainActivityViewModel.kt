package com.lolhistory

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lolhistory.datamodel.Account
import com.lolhistory.datamodel.MatchHistory
import com.lolhistory.datamodel.SummonerIDInfo
import com.lolhistory.datamodel.SummonerRankInfo
import com.lolhistory.repository.RiotRepository
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable

class MainActivityViewModel: ViewModel() {
    private val _summonerIDInfoLiveData = MutableLiveData<SummonerIDInfo>()
    val summonerIDInfoLiveData: LiveData<SummonerIDInfo> get() = _summonerIDInfoLiveData

    private val _summonerRankInfoLiveData = MutableLiveData<SummonerRankInfo>()
    val summonerRankInfoLiveData: LiveData<SummonerRankInfo> get() = _summonerRankInfoLiveData

    private val _matchHistoriesLiveData = MutableLiveData<List<MatchHistory>>()
    val matchHistoriesLiveData: LiveData<List<MatchHistory>> get() = _matchHistoriesLiveData

    private var summonerName = ""

    private var matchHistories: ArrayList<MatchHistory> = ArrayList()


    fun getAccount(
        gameName: String,
        tagLine: String = "KR1"
    ) {
        Log.d("TESTLOG", "gameName: $gameName / tagLine: $tagLine")
        RiotRepository.getAccount(gameName, tagLine).subscribe(object : SingleObserver<Account> {
            override fun onSubscribe(d: Disposable) {
            }

            override fun onSuccess(account: Account) {
                summonerName = account.gameName
                getSummonerIdInfo(account.puuid)
            }

            override fun onError(e: Throwable) {
                Log.d("TESTLOG", "[getAccount] exception: $e")
            }
        })
    }

    fun getSummonerIdInfo(puuid: String) {
        if (puuid.isEmpty()) _summonerIDInfoLiveData.value = null
        matchHistories.clear()

        RiotRepository.getSummonerIdInfo(puuid).subscribe(object :SingleObserver<SummonerIDInfo> {
            override fun onSubscribe(d: Disposable) {
            }

            override fun onSuccess(summonerIDInfo: SummonerIDInfo) {
                _summonerIDInfoLiveData.value = summonerIDInfo
                getSummonerRankInfo(summonerIDInfo.id)
                getMatchList(summonerIDInfo.puuid)
                Log.d("TESTLOG", "[getSummonerIdInfo] id: " + summonerIDInfo.puuid)
            }

            override fun onError(e: Throwable) {
                Log.d("TESTLOG", "[getSummonerIdInfo] exception: $e")
                _summonerIDInfoLiveData.value = null
            }
        })
    }

    private fun getSummonerRankInfo(summonerId: String) {
        RiotRepository.getSummonerRankInfo(summonerId).subscribe(object: SingleObserver<List<SummonerRankInfo>> {
            override fun onSubscribe(d: Disposable) {
            }

            override fun onSuccess(summonerRankInfos: List<SummonerRankInfo>) {
                Log.d("TESTLOG", "[getSummonerRankInfo] summonerRankInfos: $summonerRankInfos")
                setSummonerRankInfo(summonerRankInfos)
            }

            override fun onError(e: Throwable) {
                Log.d("TESTLOG", "[getSummonerRankInfo] exception: $e")
            }
        })
    }

    private fun getMatchList(puuid: String) {
        RiotRepository.getMatchHistoryList(puuid, 0, 16).subscribe(object: SingleObserver<List<String>> {
            override fun onSubscribe(d: Disposable) {
            }

            override fun onSuccess(list: List<String>) {
                for (match in list) {
                    getMatchHistory(match)
                }
            }

            override fun onError(e: Throwable) {
                Log.d("TESTLOG", "[getMatchList] exception: $e")
            }
        })
    }

    private fun getMatchHistory(matchId: String) {
        RiotRepository.getMatchHistory(matchId).subscribe(object: SingleObserver<MatchHistory> {
            override fun onSubscribe(d: Disposable) {
            }

            override fun onSuccess(matchHistory: MatchHistory) {
                matchHistories.add(matchHistory)
                if (matchHistories.size > 15) {
                    _matchHistoriesLiveData.value = matchHistories
                }
            }

            override fun onError(e: Throwable) {
                Log.d("TESTLOG", "[getMatchHistory] exception: $e")
                _matchHistoriesLiveData.value = null

            }
        })
    }

    private fun setSummonerRankInfo(summonerRankInfoList: List<SummonerRankInfo>) {
        var soloRankInfo: SummonerRankInfo? = null
        var flexRankInfo: SummonerRankInfo? = null
        var soloRankTier = 0
        var flexRankTier = 0
        val summonerRankInfo: SummonerRankInfo
        when {
            summonerRankInfoList.isEmpty() -> {
                // 언랭
                Log.d("TESTLOG", "언랭")
                summonerRankInfo = SummonerRankInfo(summonerName, "", "UNRANKED", "", 0, 0, 0)
            }
            summonerRankInfoList[0].queueType == "CHERRY" -> {
                // 아레나
                summonerRankInfo = SummonerRankInfo(summonerName, "CHERRY", "UNRANKED", "", summonerRankInfoList[0].leaguePoints, summonerRankInfoList[0].wins, summonerRankInfoList[0].losses)
            }
            else -> {
                for (info in summonerRankInfoList) {
                    if (info.queueType == "RANKED_SOLO_5x5") {
                        // 솔랭
                        soloRankInfo = info
                        soloRankTier = calcTier(info.tier, info.rank, info.leaguePoints)
                    } else if (info.queueType == "RANKED_FLEX_SR") {
                        // 자랭
                        flexRankInfo = info
                        flexRankTier = calcTier(info.tier, info.rank, info.leaguePoints)
                    }
                }
                summonerRankInfo = if (soloRankTier < flexRankTier) {
                    // 자랭 티어가 더 높을 때
                    flexRankInfo!!
                } else {
                    // 솔랭 티어가 더 높거나 솔랭과 자랭의 티어가 같을 때
                    soloRankInfo!!
                }
            }
        }
        _summonerRankInfoLiveData.value = summonerRankInfo
    }

    private fun calcTier(tier: String, rank: String, lp: Int): Int {
        var tierNum = 0
        when (tier) {
            "IRON" -> { }
            "BRONZE" -> tierNum = 1000
            "SILVER" -> tierNum = 2000
            "GOLD" -> tierNum = 3000
            "PLATINUM" -> tierNum = 4000
            "EMERALD" -> tierNum = 5000
            "DIAMOND" -> tierNum = 6000
            "MASTER" -> tierNum = 7000
            "GRANDMASTER" -> tierNum = 8000
            "CHALLENGER" -> tierNum = 9000
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