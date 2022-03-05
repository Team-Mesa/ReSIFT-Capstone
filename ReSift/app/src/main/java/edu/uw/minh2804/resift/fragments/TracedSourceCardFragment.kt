package edu.uw.minh2804.resift.fragments

import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MediatorLiveData
import edu.uw.minh2804.resift.R
import edu.uw.minh2804.resift.models.Article
import edu.uw.minh2804.resift.viewmodels.SiftResultViewModel

class TracedSourceCardFragment : ArticleListCardFragment() {
    private val viewModel: SiftResultViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        labelIconView.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_traced_source))
        labelView.text = getString(R.string.traced_source_card_label)

        MediatorLiveData<Pair<Boolean, Article?>>().apply {
            addSource(viewModel.isQuerying) { value = Pair(viewModel.isQuerying.value!!, viewModel.article.value) }
            addSource(viewModel.article) { value = Pair(viewModel.isQuerying.value!!, viewModel.article.value) }
        } .observe(viewLifecycleOwner) {
            val isQuerying = it.first
            val article = it.second
            if (isQuerying) {
                TransitionManager.beginDelayedTransition(view as ViewGroup, AutoTransition())
                expandableIconView.visibility = View.GONE
                listView.visibility = View.GONE
                loadingIconView.visibility = View.VISIBLE
            } else {
                submitList(listOfNotNull(article))
            }
        }
    }
}
