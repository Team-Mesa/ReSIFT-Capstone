package edu.uw.minh2804.resift.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.uw.minh2804.resift.models.Article
import edu.uw.minh2804.resift.models.Publisher
import edu.uw.minh2804.resift.models.SiftResult
import edu.uw.minh2804.resift.services.SiftService
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SiftViewModel : ViewModel() {
	private val _isQuerying = MutableLiveData(false)
	private val _queryUrl = MutableLiveData<String>()

	private val _article = MutableLiveData<Article?>()
	private val _publisher = MutableLiveData<Publisher?>()
	private val _relatedArticles = MutableLiveData<List<Article>>()

	val isQuerying: LiveData<Boolean>
		get() = _isQuerying

	val queryUrl: LiveData<String?>
		get() = _queryUrl

	val article: LiveData<Article?>
		get() = _article

	val publisher: LiveData<Publisher?>
		get() = _publisher

	val relatedArticles: LiveData<List<Article>>
		get() = _relatedArticles

	fun siftArticle(url: String) {
		Log.v(TAG, url)
		_isQuerying.value = true
		_queryUrl.value = url
		viewModelScope.launch {
			getSiftResult(url)
			getRelatedArticles(url)
			_isQuerying.value = false
		}
	}

	private suspend fun getSiftResult(url: String) {
		var result: SiftResult? = null
		try {
			result = SiftService.getSiftResult(url)
		} catch (e: Exception) {
			Log.e(TAG, e.toString())
		}
		_article.value = result?.article
		_publisher.value = result?.publisher
	}

	private suspend fun getRelatedArticles(url: String) {
		var result: List<Article>? = null
		try {
			result = SiftService.getRelatedArticles(url)
		} catch (e: Exception) {
			Log.e(TAG, e.toString())
		}
		_relatedArticles.value = result ?: listOf()
	}

	companion object {
		private val TAG = SiftViewModel::class.simpleName
	}
}
