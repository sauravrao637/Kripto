package com.camo.kripto.ui.presentation.about

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.camo.kripto.databinding.FragContirbutorsBinding
import com.camo.kripto.remote.api.GHApiHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class FragContributors : Fragment() {

    @Inject lateinit var ghApiHelper: GHApiHelper
    private lateinit var binding: FragContirbutorsBinding
    private lateinit var adapter: ContributorAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragContirbutorsBinding.inflate(inflater,container,false)
        adapter = ContributorAdapter()
        adapter.setData(null)
        setupUI()
        getContributors()
        return binding.root
    }

    var contributorJob: Job? = null
    private fun getContributors() {
        contributorJob?.cancel()
        contributorJob = CoroutineScope(Dispatchers.IO).launch {
            var contributors: Contributors? = null
            try {
                contributors = ghApiHelper.getContributors()
            } catch (e: Exception) {
                Timber.d(e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "Ooops Something went wrong",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            withContext(Dispatchers.Main) { adapter.setData(contributors) }
        }
    }

    private fun setupUI() {
        binding.rvFragContributors.layoutManager = LinearLayoutManager(context)
        binding.rvFragContributors.adapter = adapter
    }
}