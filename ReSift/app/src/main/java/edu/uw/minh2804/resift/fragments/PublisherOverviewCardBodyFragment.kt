package edu.uw.minh2804.resift.fragments

import android.graphics.BitmapFactory
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
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
		val expandToggleView = historyView.findViewById<ImageButton>(com.ms.square.android.expandabletextview.R.id.expand_collapse)
		expandToggleView.apply {
			val surfaceColor = TypedValue()
			requireContext().theme.resolveAttribute(com.google.android.material.R.attr.colorSurface, surfaceColor, true)
			background = GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, intArrayOf(surfaceColor.data, 0xFFF))
			layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
				setMargins(0, -resources.getDimension(R.dimen.margin_small).toInt(), 0, 0)
			}
		}

		viewModel.publisher.observe(viewLifecycleOwner) { publisher ->
			if (publisher != null) {
				biasRatingView.text = publisher.biasRating ?: getString(R.string.publisher_overview_card_body_bias_rating_not_found)
				historyView.text = publisher.history?.let { it + '\n' } ?: getString(R.string.publisher_overview_card_body_history_not_found)
				if (publisher.credibilityRating != null) {
					val credibilityRating = publisher.credibilityRating.toString() + " / 5"
					credibilityRatingView.text = credibilityRating
				} else {
					credibilityRatingView.text = getString(R.string.publisher_overview_card_body_credibility_rating_not_found)
				}
			}
		}

		val faviconView = view.findViewById<ShapeableImageView>(R.id.shapeable_image_view_publisher_overview_card_body_favicon)
		viewModel.article.observe(viewLifecycleOwner) {
			val statusColor = TypedValue()
			if (it?.favicon != null) {
				val base64Image = it.favicon.slice(IntRange(2, it.favicon.length - 2))
				val decodedImage = Base64.getDecoder().decode(base64Image)
				faviconView.setImageBitmap(BitmapFactory.decodeByteArray(decodedImage, 0, decodedImage.size))
				requireContext().theme.resolveAttribute(com.google.android.material.R.attr.colorSecondary, statusColor, true)
			} else {
				faviconView.setImageResource(R.drawable.ic_all_error_image_not_found)
				requireContext().theme.resolveAttribute(com.google.android.material.R.attr.colorError, statusColor, true)
			}
			faviconView.setBackgroundColor(statusColor.data)
		}
	}
}
