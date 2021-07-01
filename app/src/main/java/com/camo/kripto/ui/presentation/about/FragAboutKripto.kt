package com.camo.kripto.ui.presentation.about

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.camo.kripto.Constants
import com.camo.kripto.databinding.FragAboutKriptoBinding
import com.camo.kripto.utils.Extras

class FragAboutKripto : Fragment() {
    private lateinit var binding: FragAboutKriptoBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragAboutKriptoBinding.inflate(inflater,container,false)
        binding.button2.setOnClickListener {
            Extras.browse(Constants.GH_URL, context)
        }
        return binding.root
    }
}