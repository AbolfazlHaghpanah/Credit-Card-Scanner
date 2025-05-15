package com.haghpanah.scanner.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.haghpanah.creditcardscanner.CreditCardScanner
import com.haghpanah.scanner.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.startScan.setOnClickListener {
            CreditCardScanner.startActivity(this)
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