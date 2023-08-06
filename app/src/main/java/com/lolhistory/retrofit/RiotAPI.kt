package com.lolhistory.retrofit

import com.lolhistory.datamodel.MatchHistory
import com.lolhistory.datamodel.MatchList
import com.lolhistory.datamodel.SummonerIDInfo
import com.lolhistory.datamodel.SummonerRankInfo
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

interface RiotAPI {
    // 아이디 정보
    @Headers("Content-Type: application/json;charset=utf-8", "Accept: application/json", "X-Riot-Token: " + BaseUrl.RIOT_API_KEY)
    @GET(BaseUrl.RIOT_API_GET_SUMMONER + "{userId}")
    fun getSummonerIdInfo(@Path("userId") userId: String): Single<SummonerIDInfo>

    // 랭크
    @Headers("Content-Type: application/json;charset=utf-8", "Accept: application/json", "X-Riot-Token: " + BaseUrl.RIOT_API_KEY)
    @GET(BaseUrl.RIOT_API_GET_RANK + "{userId}")
    fun getSummonerRankInfo(@Path("userId") userId: String): Single<List<SummonerRankInfo>>

    // 매치 리스트
    @Headers("Content-Type: application/json;charset=utf-8", "Accept: application/json", "X-Riot-Token: " + BaseUrl.RIOT_API_KEY)
    @GET(BaseUrl.RIOT_API_GET_MATCH_LIST + "{puuid}/ids?start=0&count=20")
    fun getMatchHistoryList(@Path("puuid") accountId: String): Single<ArrayList<String>>

    // 매치 히스토리
    @Headers("Content-Type: application/json;charset=utf-8", "Accept: application/json", "X-Riot-Token: " + BaseUrl.RIOT_API_KEY)
    @GET(BaseUrl.RIOT_API_GET_MATCH + "{matchId}")
    fun getMatchHistory(@Path("matchId") matchId: String): Single<MatchHistory>
}