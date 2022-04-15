package edu.uw.minh2804.resift.fragments

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import edu.uw.minh2804.resift.R

class HomeFragment : Fragment(R.layout.fragment_home) {
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		view.findViewById<Button>(R.id.button_home_search).setOnClickListener {
			findNavController().navigate(R.id.action_HomeFragment_to_SiftFragment)
		}
	}
}
