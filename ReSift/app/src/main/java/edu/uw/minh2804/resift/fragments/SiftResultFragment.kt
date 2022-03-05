package edu.uw.minh2804.resift.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.button.MaterialButton
import edu.uw.minh2804.resift.R
import edu.uw.minh2804.resift.viewmodels.SiftResultViewModel

class SiftResultFragment : Fragment(R.layout.fragment_sift_result) {
    private val viewModel: SiftResultViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val shareButtonView = view.findViewById<MaterialButton>(R.id.material_button_sift_result_share)
        viewModel.queryUrl.observe(viewLifecycleOwner) {
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, it)
            }
            val shareIntent = Intent.createChooser(sendIntent, null)
            shareButtonView.setOnClickListener { startActivity(shareIntent) }
        }
    }
}
