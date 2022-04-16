package edu.uw.minh2804.resift

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import edu.uw.minh2804.resift.viewmodels.SiftViewModel

class MainActivity : AppCompatActivity(R.layout.activity_main) {
	private val viewModel: SiftViewModel by viewModels()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		viewModel.url.value ?: handleIntent(intent)
		setSupportActionBar(findViewById(R.id.toolbar_main)).also {
			supportActionBar!!.setDisplayShowTitleEnabled(false)
		}
		val progressBar = findViewById<FrameLayout>(R.id.frame_layout_main_progress_bar)
		viewModel.isQuerying.observe(this) {
			progressBar.visibility = if (it) View.VISIBLE else View.GONE
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
