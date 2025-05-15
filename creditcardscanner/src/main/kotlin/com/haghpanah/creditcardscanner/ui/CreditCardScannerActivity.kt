package com.haghpanah.creditcardscanner.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.haghpanah.creditcardscanner.ui.fragment.FragmentCreditCardScannerContent
import com.haghpanah.scanner.databinding.ActivityCreditCardScannerBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreditCardScannerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreditCardScannerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCreditCardScannerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.commit {
            setReorderingAllowed(true)
            add(binding.contentFragmentContainer.id, FragmentCreditCardScannerContent())
        }
    }
}