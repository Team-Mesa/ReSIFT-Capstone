package edu.uw.minh2804.resift.fragments

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import edu.uw.minh2804.resift.R
import edu.uw.minh2804.resift.viewmodels.SiftViewModel

abstract class CardFragment : Fragment(R.layout.fragment_card) {
	protected lateinit var titleView: TextView
	protected val viewModel: SiftViewModel by activityViewModels()

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		titleView = view.findViewById(R.id.text_view_card_title)
	}
}
