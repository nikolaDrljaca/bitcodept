package com.drbrosdev.qrscannerfromlib.ui.localimage

import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.drbrosdev.qrscannerfromlib.R
import com.drbrosdev.qrscannerfromlib.databinding.FragmentLocalImageBinding
import com.drbrosdev.qrscannerfromlib.ui.epoxy.localImageHeader
import com.drbrosdev.qrscannerfromlib.ui.epoxy.localImageInfo
import com.drbrosdev.qrscannerfromlib.util.updateWindowInsets
import com.drbrosdev.qrscannerfromlib.util.viewBinding
import com.google.android.material.transition.MaterialSharedAxis
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.common.InputImage
import logcat.logcat
import org.koin.android.ext.android.inject

class LocalImageFragment: Fragment(R.layout.fragment_local_image) {
    private val binding: FragmentLocalImageBinding by viewBinding()
    private val scanner: BarcodeScanner by inject()

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
                    barcodes.forEach {
                        logcat { "code: ${it.rawValue}" }
                    }
                }
                .addOnFailureListener {
                    /*
                    Display an error message.
                    If the resulting list is empty also display a message informing the user.
                     */
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

        binding.apply {
            rvLocalCodes.withModels {
                localImageHeader { id("local_image_header") }
                localImageInfo {
                    id("local_image_info")
                    infoText(getString(R.string.local_image_scanning_info))
                    onSelectImageClicked {
                        selectImageIntent.launch("image/*")
                    }
                }
            }
        }
    }
}