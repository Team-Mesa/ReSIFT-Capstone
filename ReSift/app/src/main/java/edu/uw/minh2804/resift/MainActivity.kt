package edu.uw.minh2804.resift

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import edu.uw.minh2804.resift.viewmodels.SiftViewModel
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity(R.layout.activity_main) {
	private val viewModel: SiftViewModel by viewModels()
	private var themePreference by Delegates.notNull<Int>()
	private lateinit var optionsMenu: Menu

	override fun onCreate(savedInstanceState: Bundle?) {
		themePreference = getPreferences(Context.MODE_PRIVATE).getInt(getString(R.string.key_setting_theme), -1)
		when (themePreference) {
			R.id.action_theme_light -> setTheme(R.style.Theme_App_Light)
			R.id.action_theme_dark -> setTheme(R.style.Theme_App_Dark)
			R.id.action_theme_spring -> setTheme(R.style.Theme_App_Spring)
			R.id.action_theme_ukraine -> setTheme(R.style.Theme_App_Ukraine)
			else -> setTheme(R.style.Theme_App_DayNight)
		}
		super.onCreate(savedInstanceState)
		viewModel.url.value ?: handleIntent(intent)
		setSupportActionBar(findViewById(R.id.toolbar_main))
		val progressBar = findViewById<FrameLayout>(R.id.frame_layout_main_progress_bar)
		viewModel.isQuerying.observe(this) {
			progressBar.visibility = if (it) View.VISIBLE else View.GONE
		}
	}

	override fun onCreateOptionsMenu(menu: Menu?): Boolean {
		menuInflater.inflate(R.menu.activity_main, menu)
		optionsMenu = menu!!
		if (themePreference != -1) {
			optionsMenu.findItem(themePreference)?.apply { isChecked = true }
		} else {
			optionsMenu.findItem(R.id.action_theme_system_default).apply { isChecked = true }
		}
		return true
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		when (item.itemId) {
			R.id.action_support_ukraine -> {
				val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://donate.redcrossredcrescent.org/ua/donate"))
				startActivity(browserIntent)
			}
			R.id.action_about -> {
				val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://dmiau88.github.io/ReSIFT-Website"))
				startActivity(browserIntent)
			}
			R.id.action_theme_system_default -> updateTheme(R.id.action_theme_system_default)
			R.id.action_theme_light -> updateTheme(R.id.action_theme_light)
			R.id.action_theme_dark -> updateTheme(R.id.action_theme_dark)
			R.id.action_theme_spring -> updateTheme(R.id.action_theme_spring)
			R.id.action_theme_ukraine -> updateTheme(R.id.action_theme_ukraine)
		}
		return super.onOptionsItemSelected(item)
	}

	override fun onNewIntent(intent: Intent?) {
		super.onNewIntent(intent)
		handleIntent(intent)
	}

	private fun updateTheme(id: Int) {
		with(getPreferences(Context.MODE_PRIVATE).edit()) {
			putInt(getString(R.string.key_setting_theme), id)
			apply()
		}
		recreate()
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
