package com.drbrosdev.qrscannerfromlib.ui.createcode

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.drbrosdev.qrscannerfromlib.R
import com.drbrosdev.qrscannerfromlib.database.QRCodeEntity
import com.drbrosdev.qrscannerfromlib.databinding.FragmentCreateCodeBinding
import com.drbrosdev.qrscannerfromlib.model.QRCodeModel
import com.drbrosdev.qrscannerfromlib.network.CreateQRCodeRequest
import com.drbrosdev.qrscannerfromlib.util.*
import com.google.android.material.transition.MaterialSharedAxis
import org.koin.androidx.viewmodel.ext.android.viewModel

class CreateCodeFragment: Fragment(R.layout.fragment_create_code) {
    private val binding: FragmentCreateCodeBinding by viewBinding()
    private val viewModel: CreateCodeViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateWindowInsets(binding.root)

        val loadingDialog = createLoadingDialog()
        val requester = CreateQRCodeRequest()

        collectFlow(viewModel.events) {
            when(it) {
                is CreateCodeEvents.ShowCodeSaved -> {
                    loadingDialog.hide()
                    findNavController().navigateUp()
                }
                is CreateCodeEvents.ShowLoading -> {
                    loadingDialog.show()
                }
                is CreateCodeEvents.ShowError -> {
                    loadingDialog.hide()
                    showSnackbarShort(
                        message = "Creation failed. Check internet connection.",
                        anchor = binding.buttonCreateCode
                    )
                }
            }
        }

        binding.apply {
            imageViewBack.setOnClickListener { findNavController().navigateUp() }

            buttonCreateCode.setOnClickListener {
                //launch a create code request
                //use PlainModel
                viewModel.showLoading()
                val codeContent = editTextCodeContent.text.toString().trim()
                requester.createCall(
                    codeContent = codeContent,
                    colorInt = getColor(R.color.candy_teal),
                    onImageLoaded = {
                        val code = QRCodeEntity(
                            data = QRCodeModel.PlainModel(codeContent),
                            codeImage = it,
                            userCreated = 1
                        )
                        viewModel.insertCode(code)
                    },
                    onFail = {
                        viewModel.sendErrorEvent()
                    }
                )
            }
        }
    }
}