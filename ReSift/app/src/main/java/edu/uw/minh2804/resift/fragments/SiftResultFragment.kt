package edu.uw.minh2804.resift.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.button.MaterialButton
import edu.uw.minh2804.resift.R
import edu.uw.minh2804.resift.viewmodels.ArticleViewModel

class SiftResultFragment : Fragment(R.layout.fragment_sift_result) {
    private val viewModel: ArticleViewModel by activityViewModels()

    private var sendIntent: Intent? = null
    private var shareIntent: Intent? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.inputUrl.observe(viewLifecycleOwner) {
            sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, it)
            }
            shareIntent = Intent.createChooser(sendIntent, null)
        }

        view.findViewById<MaterialButton>(R.id.material_button_sift_result_share).setOnClickListener {
            if (shareIntent != null) {
                startActivity(shareIntent)
            }
        }
    }
}
