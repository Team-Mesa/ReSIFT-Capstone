package edu.uw.minh2804.resift.fragments

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.marginBottom
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.uw.minh2804.resift.R
import edu.uw.minh2804.resift.viewmodels.SiftResultViewModel

abstract class ListCardFragment : Fragment(R.layout.fragment_list_card) {
    private val viewModel: SiftResultViewModel by activityViewModels()

    protected lateinit var actionIconView: ImageView
    protected lateinit var labelIconView: ImageView
    protected lateinit var labelView: TextView
    protected lateinit var loadingIconView: ProgressBar
    protected lateinit var resultContainer: RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        actionIconView = view.findViewById(R.id.image_view_article_list_card_action_icon)
        labelIconView = view.findViewById(R.id.image_view_article_list_card_label_icon)
        labelView = view.findViewById(R.id.text_view_article_list_card_label)
        loadingIconView = view.findViewById(R.id.progress_bar_article_list_card_loading_icon)
        resultContainer = view.findViewById<RecyclerView>(R.id.recycler_view_article_list_card_list).apply {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerDecoration(ContextCompat.getDrawable(context, R.drawable.shape_divider)!!))
            addItemDecoration(TopMarginDecoration())
        }

        viewModel.isQuerying.observe(viewLifecycleOwner) {
            if (it) {
                TransitionManager.beginDelayedTransition(view as ViewGroup, AutoTransition())
                actionIconView.visibility = View.GONE
                loadingIconView.visibility = View.VISIBLE
            }
        }
    }

    protected fun toggleList() {
        TransitionManager.beginDelayedTransition(resultContainer, AutoTransition())
        if (resultContainer.visibility == View.GONE) {
            resultContainer.visibility = View.VISIBLE
            actionIconView.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_collapse_action)!!)
        } else {
            resultContainer.visibility = View.GONE
            actionIconView.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_expand_action)!!)
        }
    }
}

class DividerDecoration(private val divider: Drawable) : RecyclerView.ItemDecoration() {
    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)

        val dividerLeft: Int = parent.paddingLeft
        val dividerRight: Int = parent.width - parent.paddingRight

        for (i in 0..parent.childCount - 2) {
            val child: View = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams

            val dividerTop = child.bottom + params.bottomMargin
            val dividerBottom: Int = dividerTop + divider.intrinsicHeight

            divider.setBounds(dividerLeft, dividerTop, dividerRight, dividerBottom)
            divider.draw(c)
        }
    }
}

class TopMarginDecoration : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        if (parent.getChildAdapterPosition(view) != 0) {
            outRect.top = view.marginBottom
        }
    }
}
