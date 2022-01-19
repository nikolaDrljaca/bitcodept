package com.drbrosdev.qrscannerfromlib.ui.createcode

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.drbrosdev.qrscannerfromlib.R
import com.drbrosdev.qrscannerfromlib.databinding.FragmentCreateCodeBinding
import com.drbrosdev.qrscannerfromlib.ui.epoxy.createCodeType
import com.drbrosdev.qrscannerfromlib.ui.epoxy.localImageInfo
import com.drbrosdev.qrscannerfromlib.util.decideQrCodeImage
import com.drbrosdev.qrscannerfromlib.util.getCodeColorListAsMap
import com.drbrosdev.qrscannerfromlib.util.hideKeyboard
import com.drbrosdev.qrscannerfromlib.util.updateWindowInsets
import com.drbrosdev.qrscannerfromlib.util.viewBinding
import com.google.android.material.transition.MaterialSharedAxis
import org.koin.androidx.viewmodel.ext.android.viewModel

class CreateCodeFragment: Fragment(R.layout.fragment_create_code) {
    private val binding by viewBinding(FragmentCreateCodeBinding::bind)
    private val viewModel: CreateCodeViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateWindowInsets(binding.root)

        val list = viewModel.createCodeItems(getCodeColorListAsMap())

        binding.recyclerView.setItemSpacingPx(12)
        binding.recyclerView.withModels {
            localImageInfo {
                id("simple_header")
                infoText(getString(R.string.create_code_message))
                spanSizeOverride { totalSpanCount, _, _ ->
                    totalSpanCount
                }
            }

            list.forEach {
                createCodeType {
                    id(it.name)
                    codeTypeName(it.name)
                    colorInt(it.colorInt)
                    imageRes(decideQrCodeImage(it.type))
                    onClick {
                        val action = CreateCodeFragmentDirections.toCreateCodeBottomSheetFragment(it.type)
                        findNavController().navigate(action)
                    }
                }
            }
        }

        binding.apply {
            imageViewBack.setOnClickListener {
                hideKeyboard()
                findNavController().navigateUp()
            }
        }
    }
}