package edu.uw.minh2804.resift.fragments

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.imageview.ShapeableImageView
import com.ms.square.android.expandabletextview.ExpandableTextView
import edu.uw.minh2804.resift.R
import edu.uw.minh2804.resift.viewmodels.SiftViewModel
import java.util.*

class PublisherOverviewCardBodyFragment : Fragment(R.layout.fragment_publisher_overview_card_body) {
	private val viewModel: SiftViewModel by activityViewModels()

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		val credibilityRatingView = view.findViewById<TextView>(R.id.text_view_publisher_overview_card_body_credibility_rating)
		val biasRatingView = view.findViewById<TextView>(R.id.text_view_publisher_overview_card_body_bias_rating)
		val historyView = view.findViewById<ExpandableTextView>(R.id.expandable_text_view_publisher_overview_card_body_history)

		viewModel.publisher.observe(viewLifecycleOwner) {
			if (it != null) {
				biasRatingView.text = it.biasRating ?: getString(R.string.publisher_overview_card_body_bias_rating_not_found)
				historyView.text = it.history ?: getString(R.string.publisher_overview_card_body_history_not_found)
				if (it.credibilityRating != null) {
					val credibilityRating = it.credibilityRating.toString() + " / 5"
					credibilityRatingView.text = credibilityRating
				} else {
					credibilityRatingView.text = getString(R.string.publisher_overview_card_body_credibility_rating_not_found)
				}
			}
		}

		val faviconView = view.findViewById<ShapeableImageView>(R.id.shapeable_image_view_publisher_overview_card_body_favicon)

		viewModel.article.observe(viewLifecycleOwner) {
			val faviconBackgroundColor = TypedValue()
			if (it?.favicon != null) {
				val base64Image = it.favicon.slice(IntRange(2, it.favicon.length - 2))
				val decodedImage = Base64.getDecoder().decode(base64Image)
				faviconView.setImageBitmap(BitmapFactory.decodeByteArray(decodedImage, 0, decodedImage.size))
				requireContext().theme.resolveAttribute(com.google.android.material.R.attr.colorSecondary, faviconBackgroundColor, true)
			} else {
				faviconView.setImageResource(R.drawable.ic_all_error_image_not_found)
				requireContext().theme.resolveAttribute(com.google.android.material.R.attr.colorError, faviconBackgroundColor, true)
			}
			faviconView.setBackgroundColor(faviconBackgroundColor.data)
		}
	}
}
