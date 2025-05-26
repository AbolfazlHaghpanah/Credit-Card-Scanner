package com.haghpanah.scanner.ui

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.haghpanah.creditcardscanner.di.CreditCardScannerProvider
import com.haghpanah.scanner.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val creditCardScanner = CreditCardScannerProvider.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            creditCardScanner.observeCreditCardData().collect {
                binding.creditCardNumber.text = it.number
            }
        }


        binding.startScan.apply {
            setOnClickListener {
                creditCardScanner.startActivity(this@MainActivity)
            }
            text = "Open CreditCard Scanner"
        }


//        val navController = findNavController(R.id.nav_host_fragment_content_credit_card_scanner)
//        appBarConfiguration = AppBarConfiguration(navController.graph)
//        setupActionBarWithNavController(navController, appBarConfiguration)
//
//        binding.fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null)
//                .setAnchorView(R.id.fab).show()
//        }

    }
}