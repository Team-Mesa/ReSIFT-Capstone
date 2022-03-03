package edu.uw.minh2804.resift.fragments

import android.os.Bundle
import android.view.View
import edu.uw.minh2804.resift.R

class TracedSourceFragment : ArticleListFragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        label = getString(R.string.traced_source_label)
    }
}
