package edu.uw.minh2804.resift.fragments

import android.content.Context
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import edu.uw.minh2804.resift.R
import edu.uw.minh2804.resift.models.Article
import java.time.LocalDate
import java.time.format.DateTimeFormatter

abstract class ArticleListCardFragment : ListCardFragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        resultContainer.apply { adapter = ArticleListAdapter(context) }
    }

    protected fun submitList(articles: List<Article>) {
        TransitionManager.beginDelayedTransition(view as ViewGroup, AutoTransition())
        actionIconView.visibility = View.VISIBLE
        loadingIconView.visibility = View.GONE
        if (articles.isEmpty()) {
            actionIconView.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_disabled_action)!!)
            requireView().setOnClickListener(null)
        } else {
            (resultContainer.adapter as ArticleListAdapter).submitList(articles)
            actionIconView.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_expand_action)!!)
            requireView().setOnClickListener { toggleList() }
        }
    }
}

class ArticleListAdapter(private val context: Context) : ListAdapter<Article, ArticleListAdapter.ViewHolder>(ArticleDiffCallback()) {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val faviconView: ImageView = view.findViewById(R.id.image_view_article_favicon)
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

        if (article.url != null) {
            val baseUrl = Regex("^.+\\.\\w+/").find(article.url)?.value
            if (baseUrl != null) {
                val faviconUrl = "${baseUrl}favicon.ico"
                Glide
                    .with(holder.itemView.context)
                    .load(faviconUrl)
                    .error(ContextCompat.getDrawable(holder.itemView.context, R.drawable.ic_image_not_found))
                    .centerCrop()
                    .into(holder.faviconView)
            }
        }

        holder.apply {
            titleView.text = article.title ?: context.getString(R.string.article_title_not_found)
            authorsView.text = if (article.authors.isNotEmpty()) article.authors.joinToString { it.name } else context.getString(R.string.article_author_not_found)
            publicationDateView.text = publishedDate ?: context.getString(R.string.article_published_date_not_found)
            summaryView.text = article.summary ?: context.getString(R.string.article_summary_not_found)
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
