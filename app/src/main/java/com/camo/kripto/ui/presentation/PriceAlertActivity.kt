package com.camo.kripto.ui.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import com.camo.kripto.R
import com.camo.kripto.databinding.ActivityPriceAlertBinding
import com.camo.kripto.ui.presentation.coin.CoinIdNameKeys.COIN_ID_KEY
import com.camo.kripto.ui.presentation.coin.CoinIdNameKeys.COIN_NAME_KEY
import com.camo.kripto.ui.viewModel.PriceAlertActivityVM
import com.camo.kripto.utils.Extras
import com.camo.kripto.utils.Status
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.math.BigDecimal

@AndroidEntryPoint
class PriceAlertActivity : BaseActivity() {

    private lateinit var binding: ActivityPriceAlertBinding
    private val viewModel: PriceAlertActivityVM by viewModels()
    private var id: String? = null
    private var name: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPriceAlertBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        id = intent.getStringExtra(COIN_ID_KEY)
        name = intent.getStringExtra(COIN_NAME_KEY)
        supportActionBar?.title = "Alert for $name"
        if (name != null && id != null) {
            viewModel.setIdName(id!!, name!!)
        }
        setupUI()
        setupObservers()
    }

    private fun setupObservers() {
        lifecycleScope.launchWhenStarted {
            viewModel.supportedCurrencies.collectLatest {
                when (it.status) {
                    Status.SUCCESS -> {
                        val list = it.data
                        if (list != null && list.isNotEmpty()) {
                            binding.ddCurrency.visibility = View.VISIBLE
                            setCurr(list.toTypedArray())
                        }
                        //TODO else  show error

                    }
                    Status.ERROR -> {
                        //TODO show error
                    }
                    Status.LOADING -> {
                        binding.ddCurrency.visibility = View.INVISIBLE
                    }
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.currentPriceInCurr.collectLatest {
                val value = Extras.getFormattedDouble(it)
                binding.tvCurrentPrice.text = value
                if (it != BigDecimal(0)) {
                    binding.editTextLessThan.setText(it.toString())
                    binding.editTextMoreThan.setText(it.toString())
                }
            }
        }
        binding.editTextMoreThan.doOnTextChanged { text, start, before, count ->
            viewModel.setMoreThan(binding.editTextMoreThan.text.toString())
        }
        binding.editTextLessThan.doOnTextChanged { text, start, before, count ->
            viewModel.setLessThan(binding.editTextLessThan.text.toString())
        }
        lifecycleScope.launchWhenStarted {
            viewModel.isInputValid.collectLatest {
                binding.btnSave.isEnabled = it
            }
        }
    }

    private fun setupUI() {
        binding.btnSave.setOnClickListener {
            val lessThan = binding.editTextLessThan.text.toString()
            val moreThan = binding.editTextMoreThan.text.toString()
            if (lessThan.isEmpty() && moreThan.isEmpty()) {
                Snackbar.make(this, binding.root, "abey saale", Snackbar.LENGTH_LONG).show()
            } else {
                val selected = binding.rbgroup.checkedRadioButtonId == R.id.rb_once
                lifecycleScope.launch(Dispatchers.IO) {
                    viewModel.insertPriceAlert(lessThan, moreThan, selected)
                }
                Snackbar.make(this, binding.root, "Alert added for $id", Snackbar.LENGTH_LONG)
                    .show()
            }
        }
        binding.btnSave.isEnabled = false
    }

    private fun setCurr(array: Array<String>) {
        val adapter = ArrayAdapter(
            this,
            R.layout.support_simple_spinner_dropdown_item,
            array
        )
        binding.tvCurrency.setAdapter(adapter)
        binding.tvCurrency.setOnItemClickListener { _, _, position, _ ->
            viewModel.currencyChanged(adapter.getItem(position).toString())
        }
        binding.tvCurrency.setText(viewModel.currency.value, false)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}