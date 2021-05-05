package com.camo.kripto.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.camo.kripto.databinding.FragExchangesBinding
//TODO
class ExchangesFrag : Fragment() {
    private lateinit var binding : FragExchangesBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        binding = FragExchangesBinding.inflate(inflater, container, false)

        setupViewModel()
        setupUI()
        setupObservers()

        return binding.root
    }

    private fun setupObservers() {
//        TODO("Not yet implemented")
    }

    private fun setupUI() {
//        TODO("Not yet implemented")
    }

    private fun setupViewModel() {
//        TODO("Not yet implemented")
    }
}