package edu.uw.minh2804.resift.fragments

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.marginBottom
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import edu.uw.minh2804.resift.R
import edu.uw.minh2804.resift.models.Article
import edu.uw.minh2804.resift.viewmodels.SiftViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class RelatedArticleCardBodyFragment : Fragment(R.layout.fragment_related_articles_card_body) {
	private val viewModel: SiftViewModel by activityViewModels()

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		val listView = (view as RecyclerView).apply {
			adapter = ArticleListAdapter(context)
			layoutManager = LinearLayoutManager(context)
			addItemDecoration(DividerDecoration(ContextCompat.getDrawable(context, R.drawable.shape_all_divider)!!))
		}

		viewModel.relatedArticles.observe(viewLifecycleOwner) {
			(listView.adapter as ArticleListAdapter).submitList(it)
		}
	}
}

class ArticleListAdapter(private val context: Context) : ListAdapter<Article, ArticleListAdapter.ViewHolder>(ArticleDiffCallback()) {
	class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
		val authorsView: TextView = view.findViewById(R.id.text_view_article_authors)
		val faviconView: ImageView = view.findViewById(R.id.image_view_article_favicon)
		val publishedDateView: TextView = view.findViewById(R.id.text_view_article_published_date)
		val titleView: TextView = view.findViewById(R.id.text_view_article_title)
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		val view = LayoutInflater.from(parent.context).inflate(R.layout.item_article, parent, false)
		return ViewHolder(view)
	}

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val article = getItem(position);

		if (article.publishedDate != null) {
			val parsedDate = LocalDate.parse(article.publishedDate, DateTimeFormatter.ISO_DATE)
			val month = parsedDate.month.toString().lowercase().replaceFirstChar(Char::uppercase)
			val date = "Published on $month ${parsedDate.dayOfMonth}, ${parsedDate.year}"
			holder.publishedDateView.text = date
		} else {
			holder.publishedDateView.text = context.getString(R.string.article_published_date_not_found)
		}

		if (article.authors.isNotEmpty()) {
			holder.authorsView.text = article.authors.joinToString { it.name }
		} else {
			holder.authorsView.text = context.getString(R.string.article_authors_not_found)
		}

		holder.titleView.text = article.title ?: context.getString(R.string.article_title_not_found)
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
