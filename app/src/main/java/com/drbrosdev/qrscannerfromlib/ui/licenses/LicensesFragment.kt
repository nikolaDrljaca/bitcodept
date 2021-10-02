package com.drbrosdev.qrscannerfromlib.ui.licenses

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.transition.Fade
import com.drbrosdev.qrscannerfromlib.R
import com.drbrosdev.qrscannerfromlib.databinding.FragmentLicensesBinding
import com.drbrosdev.qrscannerfromlib.util.updateWindowInsets
import com.drbrosdev.qrscannerfromlib.util.viewBinding

    class LicensesFragment: Fragment(R.layout.fragment_licenses) {
        private val binding by viewBinding(FragmentLicensesBinding::bind)

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            updateWindowInsets(binding.root)

            enterTransition = Fade()
            exitTransition = Fade()
        }
    }