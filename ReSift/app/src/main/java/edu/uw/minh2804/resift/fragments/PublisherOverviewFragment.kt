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
import edu.uw.minh2804.resift.R
import edu.uw.minh2804.resift.viewmodels.SiftResultViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.*

class PublisherOverviewFragment : Fragment(R.layout.fragment_publisher_overview) {
    private val viewModel: SiftResultViewModel by activityViewModels()
    private var animationScope: CoroutineScope? = null

    private lateinit var statusLabelView: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        statusLabelView = view.findViewById(R.id.text_view_publisher_overview_status_label)
        val actionIconView = view.findViewById<ImageView>(R.id.image_view_publisher_overview_action_icon)
        val loadingIconView = view.findViewById<ProgressBar>(R.id.progress_bar_publisher_overview_loading_icon)
        val resultContainerView = view.findViewById<LinearLayout>(R.id.linear_layout_publisher_overview_result_container)
        val statusBarView = view.findViewById<ConstraintLayout>(R.id.constraint_layout_publisher_overview_status_bar)

        viewModel.isQuerying.observe(viewLifecycleOwner) {
            if (it) {
                TransitionManager.beginDelayedTransition(view as ViewGroup, AutoTransition())

                actionIconView.visibility = View.GONE
                loadingIconView.visibility = View.VISIBLE
                resultContainerView.visibility = View.GONE
                statusBarView.visibility = View.VISIBLE

                animationScope = CoroutineScope(Dispatchers.Main)
                animateLoadingStatus()
            }
        }

        viewModel.publisher.observe(viewLifecycleOwner) {
            animationScope?.cancel()
            TransitionManager.beginDelayedTransition(view as ViewGroup, AutoTransition())
            loadingIconView.visibility = View.GONE
            if (it == null) {
                actionIconView.visibility = View.VISIBLE
                statusLabelView.text = getString(R.string.publisher_overview_publisher_not_found)
            } else {
                resultContainerView.visibility = View.VISIBLE
                statusBarView.visibility = View.GONE
            }
        }

        // TODO: Factor out into a fragment
        val articleTitleView = view.findViewById<TextView>(R.id.text_view_publisher_overview_article_title)
        val publishedDateView = view.findViewById<TextView>(R.id.text_view_publisher_overview_article_published_date)
        viewModel.article.observe(viewLifecycleOwner) { article ->
            articleTitleView.text = article?.title ?: getString(R.string.article_title_not_found)
            val publishedDate = article?.publishedDate?.let {
                val date = LocalDate.parse(it, DateTimeFormatter.ISO_DATE)
                "${date.month.toString().lowercase().replaceFirstChar(Char::uppercase)} ${date.dayOfMonth}, ${date.year}"
            }
            publishedDateView.text = publishedDate ?: getString(R.string.article_published_date_not_found)
        }

        // TODO: Factor out into a fragment
        val biasRatingView = view.findViewById<TextView>(R.id.text_view_publisher_overview_bias_rating)
        val credibilityRatingView = view.findViewById<TextView>(R.id.text_view_publisher_overview_credibility_rating)
        viewModel.publisher.observe(viewLifecycleOwner) {
            when (it?.biasRating) {
                1 -> biasRatingView.text = getString(R.string.publisher_overview_bias_far_left)
                2 -> biasRatingView.text = getString(R.string.publisher_overview_bias_left)
                3 -> biasRatingView.text = getString(R.string.publisher_overview_bias_center)
                4 -> biasRatingView.text = getString(R.string.publisher_overview_bias_right)
                5 -> biasRatingView.text = getString(R.string.publisher_overview_bias_far_right)
                else -> biasRatingView.text = getString(R.string.publisher_overview_bias_rating_not_found)
            }
            when (it?.credibilityRating) {
                1 -> credibilityRatingView.text = getString(R.string.publisher_overview_credibility_very_low)
                2 -> credibilityRatingView.text = getString(R.string.publisher_overview_credibility_low)
                3 -> credibilityRatingView.text = getString(R.string.publisher_overview_credibility_medium)
                4 -> credibilityRatingView.text = getString(R.string.publisher_review_credibility_high)
                5 -> credibilityRatingView.text = getString(R.string.publisher_review_credibility_very_high)
                else -> credibilityRatingView.text = getString(R.string.publisher_overview_credibility_rating_not_found)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        animationScope?.cancel()
    }

    private fun animateLoadingStatus() {
        animationScope?.launch {
            delay((ANIMATION_REFRESH_RATE_IN_SECONDS * 1000).toLong())
            statusLabelView.text = getString(R.string.publisher_overview_loading_status_1)
            delay((ANIMATION_REFRESH_RATE_IN_SECONDS * 1000).toLong())
            statusLabelView.text = getString(R.string.publisher_overview_loading_status_2)
            delay((ANIMATION_REFRESH_RATE_IN_SECONDS * 1000).toLong())
            statusLabelView.text = getString(R.string.publisher_overview_loading_status_3)
            animateLoadingStatus()
        }
    }

    companion object {
        const val ANIMATION_REFRESH_RATE_IN_SECONDS = .5
    }
}
