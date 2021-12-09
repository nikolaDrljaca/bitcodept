package com.drbrosdev.qrscannerfromlib.ui.licenses

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.transition.Fade
import com.drbrosdev.qrscannerfromlib.R
import com.drbrosdev.qrscannerfromlib.databinding.FragmentLicensesBinding
import com.drbrosdev.qrscannerfromlib.ui.epoxy.licenseItem
import com.drbrosdev.qrscannerfromlib.util.updateWindowInsets
import com.drbrosdev.qrscannerfromlib.util.viewBinding

class LicensesFragment : Fragment(R.layout.fragment_licenses) {
    private val binding by viewBinding(FragmentLicensesBinding::bind)
    private val viewModel: LicensesViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateWindowInsets(binding.root)

        enterTransition = Fade()
        exitTransition = Fade()

        binding.recyclerView.withModels {
            viewModel.licenses.forEach { license ->
                licenseItem {
                    id("item${license.title}")
                    item(license)
                    onClick {
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            data = Uri.parse(license.link)
                        }
                        startActivity(intent)
                    }
                }
            }
        }
    }
}