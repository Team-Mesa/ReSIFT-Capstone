package edu.uw.minh2804.resift.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import edu.uw.minh2804.resift.models.Article
import edu.uw.minh2804.resift.models.Publisher
import edu.uw.minh2804.resift.models.Response

class SiftResultViewModel : ViewModel() {
    private val _queryUrl = MutableLiveData<String>()
    private val _article = MutableLiveData<Article?>()
    private val _publisher = MutableLiveData<Publisher?>()
    private val _similarArticles = MutableLiveData<List<Article>>()

    val queryUrl: LiveData<String>
        get() = _queryUrl

    val article: LiveData<Article?>
        get() = _article

    val publisher: LiveData<Publisher?>
        get() = _publisher

    val similarArticles: LiveData<List<Article>>
        get() = _similarArticles

    fun search(url: String) {
        val dummyArticle = Article(null, null, null, null, null, listOf(), null)
        val dummyPublisher = Publisher("1", "CNN", null, 1, 1, null)
        val dummyResponse = Response(dummyArticle, dummyPublisher)

        _publisher.value = dummyResponse.publisher
        _article.value = dummyResponse.article
    }
}
