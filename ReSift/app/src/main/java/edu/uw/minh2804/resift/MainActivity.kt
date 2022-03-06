package edu.uw.minh2804.resift

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import edu.uw.minh2804.resift.viewmodels.SiftResultViewModel

class MainActivity : AppCompatActivity(R.layout.activity_main) {
    private val viewModel: SiftResultViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (viewModel.queryUrl.value == null) {
            handleIntent(intent)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        when (intent?.action) {
            Intent.ACTION_SEND -> {
                assert(intent.type == "text/plain")
                viewModel.siftArticle(intent.getStringExtra(Intent.EXTRA_TEXT)!!)
            }
        }
    }
}
