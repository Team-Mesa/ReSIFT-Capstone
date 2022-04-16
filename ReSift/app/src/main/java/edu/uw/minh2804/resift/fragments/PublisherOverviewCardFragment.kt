package edu.uw.minh2804.resift.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.commit
import edu.uw.minh2804.resift.R

class PublisherOverviewCardFragment : CardFragment() {
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		titleView.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_publisher_overview_card, 0, 0, 0)
		titleView.text = getString(R.string.publisher_overview_card_title)
		viewModel.publisher.observe(viewLifecycleOwner) {
			if (it != null) {
				childFragmentManager.commit {
					replace(R.id.fragment_container_view_card_body, PublisherOverviewCardBodyFragment())
				}
			} else {
				childFragmentManager.commit {
					replace(R.id.fragment_container_view_card_body, CardResultNotFoundFragment())
				}
			}
		}
	}
}
