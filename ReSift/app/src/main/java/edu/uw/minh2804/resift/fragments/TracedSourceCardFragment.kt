package edu.uw.minh2804.resift.fragments

import android.content.Context
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import edu.uw.minh2804.resift.R
import edu.uw.minh2804.resift.models.Publisher
import edu.uw.minh2804.resift.viewmodels.SiftResultViewModel

class TracedSourceCardFragment : ListCardFragment() {
    private val viewModel: SiftResultViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        labelIconView.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_traced_source))
        labelView.text = getString(R.string.traced_source_card)
        resultContainer.adapter = PublisherListAdapter(requireContext())
        viewModel.publisher.observe(viewLifecycleOwner) { submitList(listOfNotNull(it)) }
    }

    private fun submitList(publishers: List<Publisher>) {
        TransitionManager.beginDelayedTransition(view as ViewGroup, AutoTransition())
        actionIconView.visibility = View.VISIBLE
        loadingIconView.visibility = View.GONE
        if (publishers.isEmpty()) {
            actionIconView.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_disabled_action)!!)
            requireView().setOnClickListener(null)
        } else {
            (resultContainer.adapter as PublisherListAdapter).submitList(publishers)
            actionIconView.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_expand_action)!!)
            requireView().setOnClickListener { toggleList() }
        }
    }
}

class PublisherListAdapter(private val context: Context) : ListAdapter<Publisher, PublisherListAdapter.ViewHolder>(PublisherDiffCallback()) {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val faviconView: ImageView = view.findViewById(R.id.image_view_publisher_favicon)
        val nameView: TextView = view.findViewById(R.id.text_view_publisher_name)
        val historyView: TextView = view.findViewById(R.id.text_view_publisher_history)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_publisher, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val publisher = getItem(position)

        if (publisher.mbfcUrl != null) {
            val baseUrl = Regex("^.+\\.\\w+/").find(publisher.mbfcUrl)?.value
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
            nameView.text = publisher.name
            historyView.text = publisher.history ?: context.getString(R.string.publisher_history_not_found)
        }
    }
}

class PublisherDiffCallback : DiffUtil.ItemCallback<Publisher>() {
    override fun areItemsTheSame(oldItem: Publisher, newItem: Publisher): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Publisher, newItem: Publisher): Boolean {
        return oldItem == newItem
    }
}
