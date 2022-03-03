package edu.uw.minh2804.resift.fragments

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import edu.uw.minh2804.resift.R

class ArticleSummaryCardFragment : ArticleListCardFragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        labelIconView.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_article_summary))
        labelView.text = getString(R.string.article_summary_label)
    }
}