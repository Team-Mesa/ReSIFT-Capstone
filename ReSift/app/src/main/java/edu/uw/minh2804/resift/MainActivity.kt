package edu.uw.minh2804.resift

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import edu.uw.minh2804.resift.viewmodels.SiftViewModel

class MainActivity : AppCompatActivity(R.layout.activity_main) {
	private val viewModel: SiftViewModel by viewModels()
	private lateinit var optionsMenu: Menu

	override fun onCreate(savedInstanceState: Bundle?) {
		initTheme()
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

	override fun onCreateOptionsMenu(menu: Menu?): Boolean {
		menuInflater.inflate(R.menu.activity_main, menu)
		optionsMenu = menu!!
		updateThemeMenu()
		return true
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		when (item.itemId) {
			R.id.action_theme_system_default -> updateTheme(R.id.action_theme_system_default)
			R.id.action_theme_light -> updateTheme(R.id.action_theme_light)
			R.id.action_theme_dark -> updateTheme(R.id.action_theme_dark)
			R.id.action_theme_spring -> updateTheme(R.id.action_theme_spring)
		}
		return super.onOptionsItemSelected(item)
	}

	override fun onNewIntent(intent: Intent?) {
		super.onNewIntent(intent)
		handleIntent(intent)
	}

	private fun initTheme() {
		when (getPreferences(Context.MODE_PRIVATE).getInt(getString(R.string.key_setting_theme), -1)) {
			R.id.action_theme_light -> setTheme(R.style.Theme_App_Day)
			R.id.action_theme_dark -> setTheme(R.style.Theme_App_Night)
			R.id.action_theme_spring -> setTheme(R.style.Theme_App_Spring)
			else -> setTheme(R.style.Theme_App_DayNight)
		}
	}

	private fun updateTheme(id: Int) {
		val preferences = getPreferences(Context.MODE_PRIVATE)
		with(preferences.edit()) {
			putInt(getString(R.string.key_setting_theme), id)
			apply()
		}
		when (id) {
			R.id.action_theme_light -> setTheme(R.style.Theme_App_Day)
			R.id.action_theme_dark -> setTheme(R.style.Theme_App_Night)
			R.id.action_theme_spring -> setTheme(R.style.Theme_App_Spring)
			else -> setTheme(R.style.Theme_App_DayNight)
		}
		updateThemeMenu()
		recreate()
	}

	private fun updateThemeMenu() {
		when (val id = getPreferences(Context.MODE_PRIVATE).getInt(getString(R.string.key_setting_theme), -1)) {
			-1 -> optionsMenu.findItem(R.id.action_theme_system_default).apply { isChecked = true }
			else -> optionsMenu.findItem(id).apply { isChecked = true }
		}
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
