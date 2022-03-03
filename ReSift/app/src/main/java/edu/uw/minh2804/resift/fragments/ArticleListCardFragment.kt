package edu.uw.minh2804.resift.fragments

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
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.marginBottom
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.uw.minh2804.resift.R
import edu.uw.minh2804.resift.models.Article
import java.time.LocalDate
import java.time.format.DateTimeFormatter

abstract class ArticleListCardFragment : Fragment(R.layout.fragment_article_list_card) {
    protected lateinit var cardView: ConstraintLayout
    protected lateinit var expandableIconView: ImageView
    protected lateinit var labelIconView: ImageView
    protected lateinit var labelView: TextView
    protected lateinit var listView: RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dummyArticles = arrayOf(
            Article(listOf("Author 1", "Author 2", "Author 3"), "https://...", LocalDate.parse("2022-03-01", DateTimeFormatter.ISO_DATE), getString(R.string.lorem_ipsum_long), getString(R.string.lorem_ipsum)),
            Article(listOf("Author 1", "Author 2", "Author 3"), "https://...", LocalDate.parse("2022-03-01", DateTimeFormatter.ISO_DATE), getString(R.string.lorem_ipsum_long), getString(R.string.lorem_ipsum)),
            Article(listOf("Author 1", "Author 2", "Author 3"), "https://...", LocalDate.parse("2022-03-01", DateTimeFormatter.ISO_DATE), getString(R.string.lorem_ipsum_long), getString(R.string.lorem_ipsum))
        )

        cardView = view.findViewById(R.id.constraint_layout_article_list_card)
        expandableIconView = view.findViewById(R.id.image_view_article_list_card_expandable_icon)
        labelIconView = view.findViewById(R.id.image_view_article_list_card_label_icon)
        labelView = view.findViewById(R.id.text_view_article_list_card_label)
        listView = view.findViewById<RecyclerView>(R.id.recycler_view_article_list_card_list).apply {
            adapter = ArticleListCardAdapter(dummyArticles)
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerDecoration(ContextCompat.getDrawable(context, R.drawable.shape_divider)!!))
            addItemDecoration(TopMarginDecoration())
        }

        view.setOnClickListener { toggleList() }
    }

    private fun toggleList() {
        TransitionManager.beginDelayedTransition(cardView, AutoTransition())
        if (listView.visibility == View.GONE) {
            listView.visibility = View.VISIBLE
            expandableIconView.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_collapse)!!)
        } else {
            listView.visibility = View.GONE
            expandableIconView.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_expand)!!)
        }
    }
}

class ArticleListCardAdapter(private val articles: Array<Article>) : RecyclerView.Adapter<ArticleListCardAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleView: TextView = view.findViewById(R.id.text_view_article_title)
        val authorsView: TextView = view.findViewById(R.id.text_view_article_authors)
        val publishDateView: TextView = view.findViewById(R.id.text_view_article_publish_date)
        val snippetsView: TextView = view.findViewById(R.id.text_view_article_snippets)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_article, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val article = articles[position];
        val date = article.publishDate
        val publishDate = "Published on ${date.month.toString().lowercase().replaceFirstChar(Char::uppercase)} ${date.dayOfMonth}, ${date.year}"
        holder.apply {
            titleView.text = article.title
            authorsView.text = article.authors.joinToString()
            publishDateView.text = publishDate
            snippetsView.text = article.snippets
        }
    }

    override fun getItemCount(): Int {
        return articles.size
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
