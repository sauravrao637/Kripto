package com.camo.kripto.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.camo.kripto.R
import com.camo.kripto.databinding.FragMoreBinding
import com.camo.kripto.ui.about.AboutActivity

class FragMore : Fragment() {

    private lateinit var binding: FragMoreBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragMoreBinding.inflate(LayoutInflater.from(context))
        (activity as MainActivity).supportActionBar?.title =
            context?.resources?.getString(R.string.more)
        setupUI()

        return binding.root
    }


    private fun setupUI() {
        binding.bSettings.setOnClickListener {
            val intent = Intent(requireActivity(), SettingsActivity::class.java)
            requireActivity().startActivity(intent)

        }
        binding.bAbout.setOnClickListener {
            val intent = Intent(requireActivity(), AboutActivity::class.java)
            requireActivity().startActivity(intent)
        }
        binding.bGlobal.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fl_frag_holder, FragGlobal()).addToBackStack(null).commit()
        }
    }


}