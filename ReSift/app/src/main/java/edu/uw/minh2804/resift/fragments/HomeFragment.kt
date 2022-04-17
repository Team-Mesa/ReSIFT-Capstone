package edu.uw.minh2804.resift.fragments

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import edu.uw.minh2804.resift.R
import edu.uw.minh2804.resift.viewmodels.SiftViewModel

class HomeFragment : Fragment(R.layout.fragment_home) {
	private val viewModel: SiftViewModel by activityViewModels()

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		view.findViewById<Button>(R.id.button_home_debug_search_default).setOnClickListener {
			viewModel.siftArticle("https://www.reuters.com/world/europe/powerful-explosions-heard-kyiv-after-russian-warship-sinks-2022-04-15/")
		}
		view.findViewById<Button>(R.id.button_home_debug_search_error).setOnClickListener {
			viewModel.siftArticle("https://www.reuters.com/world/europe/powerful-explos-2022-04-15/")
		}
		viewModel.isQuerying.observe(viewLifecycleOwner) {
			if (it) {
				findNavController().navigate(R.id.action_HomeFragment_to_SiftFragment)
			}
		}
	}
}
