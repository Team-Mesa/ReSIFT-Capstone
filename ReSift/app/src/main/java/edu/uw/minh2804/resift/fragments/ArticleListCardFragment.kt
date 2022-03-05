package edu.uw.minh2804.resift.fragments

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.marginBottom
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import edu.uw.minh2804.resift.R
import edu.uw.minh2804.resift.models.Article
import java.time.LocalDate
import java.time.format.DateTimeFormatter

abstract class ArticleListCardFragment : Fragment(R.layout.fragment_article_list_card) {
    protected lateinit var expandableIconView: ImageView
    protected lateinit var labelIconView: ImageView
    protected lateinit var labelView: TextView
    protected lateinit var listView: RecyclerView
    protected lateinit var loadingIconView: ProgressBar

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        expandableIconView = view.findViewById(R.id.image_view_article_list_card_expandable_icon)
        labelIconView = view.findViewById(R.id.image_view_article_list_card_label_icon)
        labelView = view.findViewById(R.id.text_view_article_list_card_label)
        listView = view.findViewById<RecyclerView>(R.id.recycler_view_article_list_card_list).apply {
            adapter = ArticleListAdapter(context)
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerDecoration(ContextCompat.getDrawable(context, R.drawable.shape_divider)!!))
            addItemDecoration(TopMarginDecoration())
        }
        loadingIconView = view.findViewById(R.id.progress_bar_article_list_card_loading_icon)
    }

    protected fun submitList(articles: List<Article>) {
        TransitionManager.beginDelayedTransition(view as ViewGroup, AutoTransition())
        expandableIconView.visibility = View.VISIBLE
        loadingIconView.visibility = View.GONE
        if (articles.isEmpty()) {
            expandableIconView.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_result_not_found)!!)
            requireView().setOnClickListener(null)
        } else {
            (listView.adapter as ArticleListAdapter).submitList(articles)
            expandableIconView.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_expand)!!)
            requireView().setOnClickListener { toggleList() }
        }
    }

    private fun toggleList() {
        TransitionManager.beginDelayedTransition(listView, AutoTransition())
        if (listView.visibility == View.GONE) {
            listView.visibility = View.VISIBLE
            expandableIconView.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_collapse)!!)
        } else {
            listView.visibility = View.GONE
            expandableIconView.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_expand)!!)
        }
    }
}

class ArticleListAdapter(private val context: Context) : ListAdapter<Article, ArticleListAdapter.ViewHolder>(ArticleDiffCallback()) {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleView: TextView = view.findViewById(R.id.text_view_article_title)
        val authorsView: TextView = view.findViewById(R.id.text_view_article_authors)
        val publicationDateView: TextView = view.findViewById(R.id.text_view_article_published_date)
        val summaryView: TextView = view.findViewById(R.id.text_view_article_summary)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_article, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val article = getItem(position);

        val publishedDate = article.publishedDate?.let {
            val date = LocalDate.parse(article.publishedDate, DateTimeFormatter.ISO_DATE)
            "Published on ${date.month.toString().lowercase().replaceFirstChar(Char::uppercase)} ${date.dayOfMonth}, ${date.year}"
        }

        holder.apply {
            titleView.text = article.title ?: context.getString(R.string.article_title_not_found_label)
            authorsView.text = if (article.authors.isNotEmpty()) article.authors.joinToString { it.name } else context.getString(R.string.article_author_not_found_label)
            publicationDateView.text = publishedDate ?: context.getString(R.string.article_published_date_not_found_label)
            summaryView.text = article.summary ?: context.getString(R.string.article_summary_not_found_label)
        }
    }
}

class ArticleDiffCallback : DiffUtil.ItemCallback<Article>() {
    override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
        return oldItem.url == newItem.url
    }

    override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
        return oldItem == newItem
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
