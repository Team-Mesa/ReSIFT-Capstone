package edu.uw.minh2804.resift.fragments

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.ms.square.android.expandabletextview.ExpandableTextView
import edu.uw.minh2804.resift.R
import edu.uw.minh2804.resift.viewmodels.SiftViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ArticleSummaryCardBodyFragment : Fragment(R.layout.fragment_article_summary_card_body) {
	private val viewModel: SiftViewModel by activityViewModels()

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		val titleView = view.findViewById<TextView>(R.id.text_view_article_summary_card_body_title)
		val authorsView = view.findViewById<TextView>(R.id.text_view_article_summary_card_body_authors)
		val publishedDateView = view.findViewById<TextView>(R.id.text_view_article_summary_card_body_published_date)

		val descriptionView = view.findViewById<ExpandableTextView>(R.id.expandable_text_view_article_summary_card_body_description)
		val expandToggleView = descriptionView.findViewById<ImageButton>(com.ms.square.android.expandabletextview.R.id.expand_collapse)
		expandToggleView.apply {
			val surfaceColor = TypedValue()
			requireContext().theme.resolveAttribute(com.google.android.material.R.attr.colorSurface, surfaceColor, true)
			background = GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, intArrayOf(surfaceColor.data, 0xFFF))
			layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
				setMargins(0, -resources.getDimension(R.dimen.margin_small).toInt(), 0, 0)
			}
		}

		viewModel.article.observe(viewLifecycleOwner) { article ->
			if (article != null) {
				titleView.text = article.title ?: getString(R.string.article_title_not_found)

				if (article.authors.isNotEmpty()) {
					authorsView.text = article.authors.joinToString { it.name }
				} else {
					authorsView.text = getString(R.string.article_authors_not_found)
				}

				if (article.publishedDate != null) {
					val parsedDate = LocalDate.parse(article.publishedDate, DateTimeFormatter.ISO_DATE)
					val month = parsedDate.month.toString().lowercase().replaceFirstChar(Char::uppercase)
					val date = "Published on $month ${parsedDate.dayOfMonth}, ${parsedDate.year}"
					publishedDateView.text = date
				} else {
					publishedDateView.text = getString(R.string.article_published_date_not_found)
				}

				descriptionView.text = article.description?.let { it + '\n' } ?: getString(R.string.article_summary_card_body_description_not_found)
			}
		}
	}
}
