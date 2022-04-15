package edu.uw.minh2804.resift.fragments

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.imageview.ShapeableImageView
import edu.uw.minh2804.resift.R
import edu.uw.minh2804.resift.viewmodels.SiftViewModel

class PublisherOverviewCardBodyFragment : Fragment(R.layout.fragment_publisher_overview_card_body) {
	private val viewModel: SiftViewModel by activityViewModels()

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		val biasRatingView = view.findViewById<TextView>(R.id.text_view_publisher_overview_card_body_bias_rating)
		val credibilityRatingView = view.findViewById<TextView>(R.id.text_view_publisher_overview_card_body_credibility_rating)
		val faviconView = view.findViewById<ShapeableImageView>(R.id.shapeable_image_view_publisher_overview_card_body_favicon)
		val historyView = view.findViewById<TextView>(R.id.text_view_publisher_overview_card_body_history)

		viewModel.publisher.observe(viewLifecycleOwner) {
			if (it != null) {
				biasRatingView.text = it.biasRating
					?: getString(R.string.publisher_overview_card_body_bias_rating_not_found)
				historyView.text = it.history
					?: getString(R.string.publisher_overview_card_body_history_not_found)
				if (it.credibilityRating != null) {
					val credibilityRating = it.credibilityRating.toString() + " / 5"
					credibilityRatingView.text = credibilityRating
				} else {
					credibilityRatingView.text = getString(R.string.publisher_overview_card_body_credibility_rating_not_found)
				}
			}
		}

		viewModel.article.observe(viewLifecycleOwner) {
			if (it?.favicon != null) {
				/*
				val decodedString = Base64.decode(it.favicon.toByteArray(), Base64.DEFAULT)
				faviconView.setImageBitmap(BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size))
				*/
			} else {
				faviconView.setImageResource(R.drawable.ic_all_image_not_found)
			}
		}
	}
}
