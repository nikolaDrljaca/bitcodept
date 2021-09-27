package com.drbrosdev.qrscannerfromlib.ui.localimage

import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts.*
import androidx.fragment.app.Fragment
import com.drbrosdev.qrscannerfromlib.R
import com.drbrosdev.qrscannerfromlib.anims.fadeTo
import com.drbrosdev.qrscannerfromlib.databinding.FragmentLocalImageBinding
import com.drbrosdev.qrscannerfromlib.ui.epoxy.localImageCode
import com.drbrosdev.qrscannerfromlib.ui.epoxy.localImageHeader
import com.drbrosdev.qrscannerfromlib.ui.epoxy.localImageInfo
import com.drbrosdev.qrscannerfromlib.ui.epoxy.localImageSelectButton
import com.drbrosdev.qrscannerfromlib.util.collectStateFlow
import com.drbrosdev.qrscannerfromlib.util.decideQrCodeColor
import com.drbrosdev.qrscannerfromlib.util.updateWindowInsets
import com.drbrosdev.qrscannerfromlib.util.viewBinding
import com.google.android.material.transition.MaterialSharedAxis
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.common.InputImage
import logcat.logcat
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class LocalImageFragment: Fragment(R.layout.fragment_local_image) {
    private val binding: FragmentLocalImageBinding by viewBinding()
    private val scanner: BarcodeScanner by inject()
    private val viewModel: LocalImageViewModel by viewModel()

    private val selectImageIntent = registerForActivityResult(GetContent()) {
        it?.let { uri ->
            val image = InputImage.fromFilePath(requireContext(), uri)
            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    /*
                    Pass these into the viewModel and process them there,
                    create a mapper to the domain model we use and put them into the database,
                    or a local list.
                    Pressing delete would remove them from the local list and exiting the screen
                    saves what's left ??
                     */
                    viewModel.submitCodes(barcodes)
                }
                .addOnFailureListener { exception ->
                    /*
                    Display an error message.
                    If the resulting list is empty also display a message informing the user.
                     */
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
        updateWindowInsets(binding.root)
        /*
        Navigation transitions.
         */
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Y, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Y, false)

        collectStateFlow(viewModel.state) { state ->
            if(state.errorMessage.isNotBlank()) {
                binding.textViewError.text = state.errorMessage
                binding.textViewError.fadeTo(state.errorMessage.isNotBlank())
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
                            onItemClicked {  }
                            onDeleteClicked {  }
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
    }
}