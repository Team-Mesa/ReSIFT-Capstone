package edu.uw.minh2804.resift.fragments

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import edu.uw.minh2804.resift.R
import edu.uw.minh2804.resift.viewmodels.SiftResultViewModel

class ArticleSummaryCardFragment : ArticleListCardFragment() {
    private val viewModel: SiftResultViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        labelIconView.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_article_summary))
        labelView.text = getString(R.string.article_summary_card)
        viewModel.article.observe(viewLifecycleOwner) { submitList(listOfNotNull(it)) }
    }
}
