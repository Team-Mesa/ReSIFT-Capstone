package edu.uw.minh2804.resift.fragments

import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import edu.uw.minh2804.resift.R
import edu.uw.minh2804.resift.viewmodels.SiftResultViewModel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PublisherReviewFragment : Fragment(R.layout.fragment_publisher_review) {
    private val viewModel: SiftResultViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val loadingIconView = view.findViewById<ProgressBar>(R.id.progress_bar_publisher_review_loading_icon)
        val loadingStatusView = view.findViewById<TextView>(R.id.text_view_publisher_review_loading_status)
        val resultIconView = view.findViewById<ImageView>(R.id.image_view_publisher_review_result_icon)
        val resultView = view.findViewById<LinearLayout>(R.id.linear_layout_publisher_review_result)

        viewModel.article.observe(viewLifecycleOwner) {
            TransitionManager.beginDelayedTransition(view as ViewGroup, AutoTransition())
            if (it != null) {
                loadingStatusView.visibility = View.GONE
                resultIconView.visibility = View.GONE
                resultView.visibility = View.VISIBLE
            } else {
                loadingStatusView.text = getString(R.string.publisher_review_not_found_label)
                resultIconView.visibility = View.VISIBLE
                resultView.visibility = View.GONE
            }
            loadingIconView.visibility = View.GONE
            lifecycleScope.cancel()
        }

        animateLoadingStatus(loadingStatusView)

        val articleTitleView = view.findViewById<TextView>(R.id.text_view_publisher_review_article_title)
        val publicationDateView = view.findViewById<TextView>(R.id.text_view_publisher_review_article_publication_date)

        val biasRatingView = view.findViewById<TextView>(R.id.text_view_publisher_review_bias_rating)
        val credibilityRatingView = view.findViewById<TextView>(R.id.text_view_publisher_review_credibility_rating)

        viewModel.article.observe(viewLifecycleOwner) {
            articleTitleView.text = it?.title ?: getString(R.string.article_title_not_found_label)
            publicationDateView.text = it?.publicationDate ?: getString(R.string.article_publication_not_found_label)
        }

        viewModel.publisher.observe(viewLifecycleOwner) {
            when (it?.biasRating) {
                1 -> biasRatingView.text = getString(R.string.publisher_review_bias_far_left)
                2 -> biasRatingView.text = getString(R.string.publisher_review_bias_left)
                3 -> biasRatingView.text = getString(R.string.publisher_review_bias_center)
                4 -> biasRatingView.text = getString(R.string.publisher_review_bias_right)
                5 -> biasRatingView.text = getString(R.string.publisher_review_bias_far_right)
                else -> biasRatingView.text = getString(R.string.publisher_review_bias_rating_not_found_label)
            }
            when (it?.credibilityRating) {
                1 -> credibilityRatingView.text = getString(R.string.publisher_review_credibility_very_low)
                2 -> credibilityRatingView.text = getString(R.string.publisher_review_credibility_low)
                3 -> credibilityRatingView.text = getString(R.string.publisher_review_credibility_medium)
                4 -> credibilityRatingView.text = getString(R.string.publisher_review_credibility_high)
                5 -> credibilityRatingView.text = getString(R.string.publisher_review_credibility_very_high)
                else -> credibilityRatingView.text = getString(R.string.publisher_review_credibility_rating_not_found_label)
            }
        }
    }

    private fun animateLoadingStatus(loadingStatusView: TextView) {
        lifecycleScope.launch {
            delay((POLLING_RATE_IN_SECOND * 1000).toLong())
            loadingStatusView.text = getString(R.string.publisher_review_loading_status_1)
            delay((POLLING_RATE_IN_SECOND * 1000).toLong())
            loadingStatusView.text = getString(R.string.publisher_review_loading_status_2)
            delay((POLLING_RATE_IN_SECOND * 1000).toLong())
            loadingStatusView.text = getString(R.string.publisher_review_loading_status_3)
            animateLoadingStatus(loadingStatusView)
        }
    }

    companion object {
        const val POLLING_RATE_IN_SECOND = .5
    }
}
