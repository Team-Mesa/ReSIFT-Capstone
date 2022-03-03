package edu.uw.minh2804.resift.fragments

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import edu.uw.minh2804.resift.R

class AdditionalSourcesCardFragment : ArticleListCardFragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        labelIconView.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_additional_sources))
        labelView.text = getString(R.string.additional_sources_label)
    }
}