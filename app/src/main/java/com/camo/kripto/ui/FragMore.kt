package com.camo.kripto.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.camo.kripto.R
import com.camo.kripto.databinding.FragMoreBinding

class FragMore: Fragment() {

    private lateinit var binding: FragMoreBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragMoreBinding.inflate(LayoutInflater.from(context))
        (activity as MainActivity).supportActionBar?.title = context?.resources?.getString(R.string.more)
        setupUI()

        return binding.root
    }


    private fun setupUI() {
        binding.bSettings.setOnClickListener {
            val intent = Intent(requireActivity(),SettingsActivity::class.java)
            requireActivity().startActivity(intent)

        }
//        TODO("Not yet implemented")
    }



}