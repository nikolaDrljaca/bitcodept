package com.drbrosdev.qrscannerfromlib.ui.localimage

import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts.*
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.drbrosdev.qrscannerfromlib.R
import com.drbrosdev.qrscannerfromlib.anims.fadeTo
import com.drbrosdev.qrscannerfromlib.databinding.FragmentLocalImageBinding
import com.drbrosdev.qrscannerfromlib.ui.epoxy.localImageCode
import com.drbrosdev.qrscannerfromlib.ui.epoxy.localImageHeader
import com.drbrosdev.qrscannerfromlib.ui.epoxy.localImageInfo
import com.drbrosdev.qrscannerfromlib.ui.epoxy.localImageSelectButton
import com.drbrosdev.qrscannerfromlib.util.*
import com.google.android.material.transition.MaterialSharedAxis
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.launch
import logcat.logcat
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class LocalImageFragment : Fragment(R.layout.fragment_local_image) {
    private val binding by viewBinding(FragmentLocalImageBinding::bind)
    private val scanner: BarcodeScanner by inject()
    private val viewModel: LocalImageViewModel by viewModel()

    private val selectImageIntent = registerForActivityResult(GetContent()) {
        it?.let { uri ->
            val image = InputImage.fromFilePath(requireContext(), uri)
            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    viewModel.submitBarcodes(barcodes, getCodeColorListAsMap())
                }
                .addOnFailureListener { exception ->
                    viewModel.submitError(exception.localizedMessage ?: "")
                }
                .addOnCanceledListener {
                    viewModel.submitError("No images chosen.")
                }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /*
        Window setup.
         */
        val statusBarColor = ContextCompat.getColor(requireContext(), R.color.background)
        requireActivity().window.statusBarColor = statusBarColor
        updateWindowInsets(binding.root)
        /*
        Navigation transitions.
         */
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Y, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Y, false)

        val loadingDialog = createLoadingDialog()

        viewModel.state.observe(viewLifecycleOwner) { state ->
            if (!state.isListEmpty) loadingDialog.dismiss()
            if (state.errorMessage.isNotBlank()) {
                binding.textViewError.apply {
                    text = state.errorMessage
                    fadeTo(state.errorMessage.isNotBlank())
                    setBackgroundColor(getColor(R.color.candy_red))
                }
            }

            binding.rvLocalCodes.withModels {
                localImageHeader { id("local_image_header") }
                if (state.isListEmpty) localImageInfo {
                    id("local_image_info")
                    infoText(getString(R.string.local_image_scanning_info))
                }
                state.codes.forEach { qrCodeEntity ->
                    qrCodeEntity?.let { code ->
                        localImageCode {
                            id(code.id)
                            item(code)
                            colorInt(decideQrCodeColor(code))
                            onItemClicked { navigateToDetail(it.id) }
                            onDeleteClicked { viewModel.deleteLocalDetectedCode(it) }
                        }
                    }
                }
                localImageSelectButton {
                    id("local_image_select_button")
                    onClick {
                        selectImageIntent.launch("image/*")
                    }
                }
            }
        }

        collectFlow(viewModel.events) {
            when (it) {
                LocalImageEvents.ShowLoading -> {
                    loadingDialog.show()
                }
                LocalImageEvents.ShowEmptyResult -> {
                    showSnackbarShort("No codes were found.")
                }
            }
        }
    }

    private fun navigateToDetail(codeId: Int) {
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
        val arg = bundleOf("code_id" to codeId)
        findNavController().navigate(R.id.action_localImageFragment_to_codeDetailFragment, arg)
    }
}