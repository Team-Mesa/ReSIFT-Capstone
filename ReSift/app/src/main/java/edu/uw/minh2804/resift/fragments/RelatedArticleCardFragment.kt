package edu.uw.minh2804.resift.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.commit
import edu.uw.minh2804.resift.R

class RelatedArticleCardFragment : CardFragment() {
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		titleView.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_related_articles_card, 0, 0, 0)
		titleView.text = getString(R.string.related_articles_card_title)
		viewModel.relatedArticles.observe(viewLifecycleOwner) {
			if (it.isNotEmpty()) {
				childFragmentManager.commit {
					replace(R.id.fragment_container_view_card_body, RelatedArticleCardBodyFragment())
				}
			} else {
				childFragmentManager.commit {
					replace(R.id.fragment_container_view_card_body, CardResultNotFoundFragment())
				}
			}
		}
	}
}
