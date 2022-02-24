package edu.uw.minh2804.resift

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ArticleViewModel : ViewModel() {
    private val _inputUrl = MutableLiveData<String>()

    val inputUrl: LiveData<String>
        get() = _inputUrl

    fun search(url: String) {
        _inputUrl.value = url
    }
}
