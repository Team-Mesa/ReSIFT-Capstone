package edu.uw.minh2804.resift.fragments

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputLayout
import edu.uw.minh2804.resift.MainActivity
import edu.uw.minh2804.resift.R
import edu.uw.minh2804.resift.viewmodels.SiftViewModel

class HomeFragment : Fragment(R.layout.fragment_home) {
	private val viewModel: SiftViewModel by activityViewModels()

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		val searchBar = view.findViewById<TextInputLayout>(R.id.text_input_layout_home_search_bar)
		searchBar.setEndIconOnClickListener { viewModel.siftArticle(searchBar.editText!!.text.toString()) }
		searchBar.editText!!.setOnEditorActionListener { v, actionId, _ ->
			if (actionId == EditorInfo.IME_ACTION_SEARCH) {
				viewModel.siftArticle(v.text.toString())
				return@setOnEditorActionListener true
			}
			return@setOnEditorActionListener false
		}
		viewModel.isQuerying.observe(viewLifecycleOwner) {
			if (it) {
				findNavController().navigate(R.id.action_HomeFragment_to_SiftFragment)
			}
		}
		val mainActivity = requireActivity() as MainActivity
		mainActivity.supportActionBar!!.setDisplayHomeAsUpEnabled(false)
		mainActivity.findViewById<TextView>(R.id.text_view_toolbar_main_app_name).apply {
			layoutParams = Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.WRAP_CONTENT).apply {
				gravity = Gravity.START
			}
		}
	}
}
