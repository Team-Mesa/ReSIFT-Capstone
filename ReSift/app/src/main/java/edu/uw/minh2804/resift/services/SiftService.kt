package edu.uw.minh2804.resift.services

import edu.uw.minh2804.resift.models.Article
import edu.uw.minh2804.resift.models.SiftResult
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import okhttp3.OkHttpClient
import retrofit2.*
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

object SiftService {
	private const val BASE_URL = "https://resift-adtk.herokuapp.com/"

	private interface Api {
		@GET("articleInfo")
		fun getSiftResult(@Query("url") articleUrl: String): Call<SiftResult>

		@GET("similarArticles")
		fun getRelatedArticles(@Query("url") articleUrl: String): Call<List<Article>>
	}

	private val api: Api by lazy {
		val client = OkHttpClient
			.Builder()
			.connectTimeout(5, TimeUnit.MINUTES)
			.readTimeout(1, TimeUnit.MINUTES)
			.writeTimeout(1, TimeUnit.MINUTES)
			.build()
		Retrofit
			.Builder()
			.addConverterFactory(MoshiConverterFactory.create())
			.baseUrl(BASE_URL)
			.client(client)
			.build()
			.create(Api::class.java)
	}

	suspend fun getSiftResult(articleUrl: String): SiftResult {
		return suspendCoroutine {
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

	suspend fun getRelatedArticles(articleUrl: String): List<Article> {
		return suspendCoroutine {
			api.getRelatedArticles(articleUrl).enqueue(object : Callback<List<Article>> {
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
}
