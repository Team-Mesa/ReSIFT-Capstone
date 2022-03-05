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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.lifecycleScope
import edu.uw.minh2804.resift.R
import edu.uw.minh2804.resift.models.Publisher
import edu.uw.minh2804.resift.viewmodels.SiftResultViewModel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class PublisherOverviewFragment : Fragment(R.layout.fragment_publisher_overview) {
    private val viewModel: SiftResultViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val loadingIconView = view.findViewById<ProgressBar>(R.id.progress_bar_publisher_overview_loading_icon)
        val resultNotFoundIconView = view.findViewById<ImageView>(R.id.image_view_publisher_overview_result_not_found_icon)
        val resultView = view.findViewById<LinearLayout>(R.id.linear_layout_publisher_overview_result)
        val statusBarView = view.findViewById<ConstraintLayout>(R.id.constraint_layout_publisher_overview_status_bar)
        val statusLabelView = view.findViewById<TextView>(R.id.text_view_publisher_overview_status_label)

        MediatorLiveData<Pair<Boolean, Publisher?>>().apply {
            addSource(viewModel.isQuerying) { value = Pair(viewModel.isQuerying.value!!, viewModel.publisher.value) }
            addSource(viewModel.publisher) { value = Pair(viewModel.isQuerying.value!!, viewModel.publisher.value) }
        } .observe(viewLifecycleOwner) {
            TransitionManager.beginDelayedTransition(view as ViewGroup, AutoTransition())
            val isQuerying = it.first
            val publisher = it.second
            if (isQuerying) {
                loadingIconView.visibility = View.VISIBLE
                resultNotFoundIconView.visibility = View.GONE
                resultView.visibility = View.GONE
                statusBarView.visibility = View.VISIBLE
                animateLoadingStatus(statusLabelView)
            } else {
                lifecycleScope.cancel()
                loadingIconView.visibility = View.GONE
                if (publisher == null) {
                    resultNotFoundIconView.visibility = View.VISIBLE
                    statusLabelView.text = getString(R.string.publisher_overview_publisher_not_found_label)
                } else {
                    resultView.visibility = View.VISIBLE
                    statusBarView.visibility = View.GONE
                }
            }
        }

        // TODO: Factor out into a fragment
        val articleTitleView = view.findViewById<TextView>(R.id.text_view_publisher_overview_article_title)
        val publishedDateView = view.findViewById<TextView>(R.id.text_view_publisher_overview_article_published_date)
        viewModel.article.observe(viewLifecycleOwner) { article ->
            articleTitleView.text = article?.title ?: getString(R.string.article_title_not_found_label)
            val publishedDate = article?.publishedDate?.let {
                val date = LocalDate.parse(it, DateTimeFormatter.ISO_DATE)
                "${date.month.toString().lowercase().replaceFirstChar(Char::uppercase)} ${date.dayOfMonth}, ${date.year}"
            }
            publishedDateView.text = publishedDate ?: getString(R.string.article_published_date_not_found_label)
        }

        // TODO: Factor out into a fragment
        val biasRatingView = view.findViewById<TextView>(R.id.text_view_publisher_overview_bias_rating)
        val credibilityRatingView = view.findViewById<TextView>(R.id.text_view_publisher_overview_credibility_rating)
        viewModel.publisher.observe(viewLifecycleOwner) {
            when (it?.biasRating) {
                1 -> biasRatingView.text = getString(R.string.publisher_overview_bias_far_left_label)
                2 -> biasRatingView.text = getString(R.string.publisher_overview_bias_left_label)
                3 -> biasRatingView.text = getString(R.string.publisher_overview_bias_center_label)
                4 -> biasRatingView.text = getString(R.string.publisher_overview_bias_right_label)
                5 -> biasRatingView.text = getString(R.string.publisher_overview_bias_far_right_label)
                else -> biasRatingView.text = getString(R.string.publisher_overview_bias_rating_not_found_label)
            }
            when (it?.credibilityRating) {
                1 -> credibilityRatingView.text = getString(R.string.publisher_overview_credibility_very_low_label)
                2 -> credibilityRatingView.text = getString(R.string.publisher_overview_credibility_low_label)
                3 -> credibilityRatingView.text = getString(R.string.publisher_overview_credibility_medium_label)
                4 -> credibilityRatingView.text = getString(R.string.publisher_review_credibility_high_label)
                5 -> credibilityRatingView.text = getString(R.string.publisher_review_credibility_very_high_label)
                else -> credibilityRatingView.text = getString(R.string.publisher_overview_credibility_rating_not_found_label)
            }
        }
    }

    private fun animateLoadingStatus(loadingStatusView: TextView) {
        lifecycleScope.launch {
            delay((ANIMATION_REFRESH_RATE_IN_SECONDS * 1000).toLong())
            loadingStatusView.text = getString(R.string.publisher_overview_loading_status_1_label)
            delay((ANIMATION_REFRESH_RATE_IN_SECONDS * 1000).toLong())
            loadingStatusView.text = getString(R.string.publisher_overview_loading_status_2_label)
            delay((ANIMATION_REFRESH_RATE_IN_SECONDS * 1000).toLong())
            loadingStatusView.text = getString(R.string.publisher_overview_loading_status_3_label)
            animateLoadingStatus(loadingStatusView)
        }
    }

    companion object {
        const val ANIMATION_REFRESH_RATE_IN_SECONDS = .5
    }
}
