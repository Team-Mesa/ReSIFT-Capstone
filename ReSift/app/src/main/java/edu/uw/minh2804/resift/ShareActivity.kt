package edu.uw.minh2804.resift

import android.app.Activity
import android.content.Intent
import android.os.Bundle

class ShareActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        assert(intent.type == "text/plain")
        val sendIntent: Intent = Intent(this, MainActivity::class.java).apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            putExtra(Intent.EXTRA_TEXT, intent.getStringExtra(Intent.EXTRA_TEXT)!!)
        }
        startActivity(sendIntent)
        finish()
    }
}
