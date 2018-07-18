package com.muheng.facebook

import com.facebook.AccessToken
import com.muheng.retrofit.Parameter
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.QueryMap
import retrofit2.http.Url
import java.util.ArrayList
import java.util.HashMap

/**
 * Created by Muheng Li on 2018/7/10.
 */
interface IFacebookApi {

    companion object {
        const val FB_API_BASE_RUL = "https://graph.facebook.com/"

        // Node
        const val NODE_ME = "me"

        // Parameter
        const val ACCESS_TOKEN = "access_token"
        const val FIELDS = "fields"

        fun getParamsMap(params: ArrayList<Parameter>): Map<String, String> {
            var paramsMap = HashMap<String, String>()

            paramsMap.put(IFacebookApi.ACCESS_TOKEN, AccessToken.getCurrentAccessToken().token)
            for (p in params) {
                if (p.value != null) {
                    paramsMap[p.key] = p.value as String
                }
            }

            return paramsMap
        }
    }

    @GET(NODE_ME)
    fun meProfile(@QueryMap params: Map<String, String>): Observable<MeProfile>

    @GET(NODE_ME)
    fun albums(@QueryMap params: Map<String, String>): Observable<ResponseAlbums>

    @GET
    fun loadMoreAlbums(@Url url: String): Observable<Albums>

    @GET("/{path}")
    fun photos(@Path("path") albumId: String, @QueryMap params: Map<String, String>): Observable<ResponsePhotos>

    @GET
    fun loadMorePhotos(@Url url: String): Observable<Photos>
}