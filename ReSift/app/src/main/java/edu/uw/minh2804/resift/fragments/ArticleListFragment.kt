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
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.marginBottom
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.uw.minh2804.resift.R
import edu.uw.minh2804.resift.models.Article
import java.time.LocalDate
import java.time.format.DateTimeFormatter

abstract class ArticleListFragment : Fragment(R.layout.fragment_article_list) {
    private lateinit var labelView: TextView
    private lateinit var listView: RecyclerView

    protected var label: CharSequence
        get() = labelView.text
        set(value) { labelView.text = value }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dummyArticles = arrayOf(
            Article(listOf("Author 1", "Author 2", "Author 3"), "https://...", LocalDate.parse("2022-03-01", DateTimeFormatter.ISO_DATE), getString(R.string.lorem_ipsum_long), getString(R.string.lorem_ipsum)),
            Article(listOf("Author 1", "Author 2", "Author 3"), "https://...", LocalDate.parse("2022-03-01", DateTimeFormatter.ISO_DATE), getString(R.string.lorem_ipsum_long), getString(R.string.lorem_ipsum)),
            Article(listOf("Author 1", "Author 2", "Author 3"), "https://...", LocalDate.parse("2022-03-01", DateTimeFormatter.ISO_DATE), getString(R.string.lorem_ipsum_long), getString(R.string.lorem_ipsum))
        )

        view.setOnClickListener { toggleListVisibility() }
        labelView = view.findViewById(R.id.text_view_article_list_label)

        listView = view.findViewById<RecyclerView>(R.id.recycler_view_article_list).apply {
            adapter = ArticleListAdapter(dummyArticles)

            layoutManager = LinearLayoutManager(context)
            //layoutManager = object : LinearLayoutManager(context) { override fun canScrollVertically(): Boolean { return false } }
            isNestedScrollingEnabled = true
            addItemDecoration(VerticalSpacingDecoration())
            addItemDecoration(DividerDecoration(ContextCompat.getDrawable(context, R.drawable.divider)!!))
        }
    }

    private fun toggleListVisibility() {
        TransitionManager.beginDelayedTransition(listView, AutoTransition())
        listView.visibility = if (listView.visibility == View.GONE) View.VISIBLE else View.GONE
    }
}

class ArticleListAdapter(private val articles: Array<Article>) : RecyclerView.Adapter<ArticleListAdapter.ViewHolder>() {
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

class VerticalSpacingDecoration : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        if (parent.getChildAdapterPosition(view) != 0) {
            outRect.top = view.marginBottom
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
