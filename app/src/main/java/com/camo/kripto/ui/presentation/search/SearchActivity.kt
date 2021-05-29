package com.camo.kripto.ui.presentation.search

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.camo.kripto.R
import com.camo.kripto.databinding.ActivitySearchBinding
import com.camo.kripto.local.model.Coin
import com.camo.kripto.repos.Repository
import com.camo.kripto.ui.adapter.SearchAdapter
import com.camo.kripto.ui.presentation.BaseActivity
import com.camo.kripto.utils.ThemeUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import timber.log.Timber
import java.net.UnknownHostException
import javax.inject.Inject

@AndroidEntryPoint
class SearchActivity : BaseActivity() {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var adapter: SearchAdapter

    @Inject
    lateinit var repository: Repository

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        supportActionBar?.hide()
        adapter = SearchAdapter()
        setupUI()

    }

    private fun setupUI() {
        binding.rvSearchResult.layoutManager = LinearLayoutManager(this)
        binding.rvSearchResult.adapter = adapter
        binding.coinSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                searchStringChanged(newText)
                Timber.d(newText)
                return false
            }

        })
    }

    var filterJob: Job? = null
    private fun searchStringChanged(newText: String?) {
        filterJob?.cancel()
        filterJob = lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val list = repository.getCoinFilterByName(newText ?: "")
                withContext(Dispatchers.Main) {
                    adapter.setData(list)
                }
            }

        }
    }
}