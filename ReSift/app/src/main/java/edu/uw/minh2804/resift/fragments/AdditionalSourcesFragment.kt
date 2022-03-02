package edu.uw.minh2804.resift.fragments

import android.os.Bundle
import android.view.View
import edu.uw.minh2804.resift.R

class AdditionalSourcesFragment : ArticleListFragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        label = getString(R.string.additional_sources_label)
    }
}
