package edu.uw.minh2804.resift.services

import edu.uw.minh2804.resift.models.Article
import edu.uw.minh2804.resift.models.SiftResult
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlinx.coroutines.withTimeout
import retrofit2.*
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

object SiftService {
    private const val BASE_URL = "https://resift-adtk.herokuapp.com/"
    private const val CONNECTION_TIMEOUT_IN_SECONDS = 30

    private interface Api {
        @GET("articleInfo")
        fun getSiftResult(@Query("url") articleUrl: String): Call<SiftResult>

        @GET("similarArticles")
        fun getSimilarArticles(@Query("url") articleUrl: String): Call<List<Article>>
    }

    private val api: Api by lazy {
        Retrofit
            .Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(Api::class.java)
    }


    suspend fun getSiftResult(articleUrl: String): SiftResult {
        var result: SiftResult
        withTimeout((CONNECTION_TIMEOUT_IN_SECONDS * 1000).toLong()) {
            result = suspendCoroutine {
                api.getSiftResult(articleUrl).enqueue(object : Callback<SiftResult> {
                    override fun onResponse(call: Call<SiftResult>, response: Response<SiftResult>) {
                        if (response.isSuccessful) {
                            it.resume(response.body()!!)
                        } else {
                            it.resumeWithException(Exception(response.errorBody().toString()))
                        }
                    }
                    override fun onFailure(call: Call<SiftResult>, t: Throwable) {
                        it.resumeWithException(Exception(t))
                    }
                })
            }
        }
        return result
    }

    suspend fun getSimilarArticles(articleUrl: String): List<Article> {
        var results: List<Article>
        withTimeout((CONNECTION_TIMEOUT_IN_SECONDS * 1000).toLong()) {
            results = suspendCoroutine {
                api.getSimilarArticles(articleUrl).enqueue(object : Callback<List<Article>> {
                    override fun onResponse(call: Call<List<Article>>, response: Response<List<Article>>) {
                        if (response.isSuccessful) {
                            it.resume(response.body()!!)
                        } else {
                            it.resumeWithException(Exception(response.errorBody().toString()))
                        }
                    }
                    override fun onFailure(call: Call<List<Article>>, t: Throwable) {
                        it.resumeWithException(Exception(t))
                    }
                })
            }
        }
        return results
    }
}
