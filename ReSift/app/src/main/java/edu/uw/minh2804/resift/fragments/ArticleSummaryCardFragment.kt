package edu.uw.minh2804.resift.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.commit
import edu.uw.minh2804.resift.R

class ArticleSummaryCardFragment : CardFragment() {
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		titleView.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_article_summary_card, 0, 0, 0)
		titleView.text = getString(R.string.article_summary_card_title)
		viewModel.publisher.observe(viewLifecycleOwner) {
			if (it != null) {
				childFragmentManager.commit {
					replace(R.id.fragment_container_view_card_body, ArticleSummaryCardBodyFragment())
				}
			} else {
				childFragmentManager.commit {
					replace(R.id.fragment_container_view_card_body, CardResultNotFoundFragment())
				}
			}
		}
	}
}
