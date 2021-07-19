package com.drbrosdev.qrscannerfromlib.ui.createcode

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.drbrosdev.qrscannerfromlib.R
import com.drbrosdev.qrscannerfromlib.databinding.FragmentCreateCodeBinding
import com.drbrosdev.qrscannerfromlib.util.updateWindowInsets
import com.drbrosdev.qrscannerfromlib.util.viewBinding
import com.google.android.material.transition.MaterialSharedAxis

class CreateCodeFragment: Fragment(R.layout.fragment_create_code) {
    private val binding: FragmentCreateCodeBinding by viewBinding()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateWindowInsets(binding.root)

        binding.apply {
            imageViewBack.setOnClickListener { findNavController().navigateUp() }

            imageViewInfo.setOnClickListener {  }
        }
    }
}