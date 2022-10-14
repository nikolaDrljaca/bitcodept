package com.drbrosdev.qrscannerfromlib.ui.localimage

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.drbrosdev.qrscannerfromlib.R
import com.drbrosdev.qrscannerfromlib.anims.fadeTo
import com.drbrosdev.qrscannerfromlib.databinding.FragmentLocalImageBinding
import com.drbrosdev.qrscannerfromlib.ui.epoxy.localImageCode
import com.drbrosdev.qrscannerfromlib.ui.epoxy.localImageHeader
import com.drbrosdev.qrscannerfromlib.ui.epoxy.localImageInfo
import com.drbrosdev.qrscannerfromlib.ui.epoxy.localImageSelectButton
import com.drbrosdev.qrscannerfromlib.util.collectFlow
import com.drbrosdev.qrscannerfromlib.util.createLoadingDialog
import com.drbrosdev.qrscannerfromlib.util.decideQrCodeColor
import com.drbrosdev.qrscannerfromlib.util.showSnackbarShort
import com.drbrosdev.qrscannerfromlib.util.updateWindowInsets
import com.drbrosdev.qrscannerfromlib.util.viewBinding
import com.google.android.material.transition.MaterialSharedAxis
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.common.InputImage
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class LocalImageFragment : Fragment(R.layout.fragment_local_image) {
    private val binding by viewBinding(FragmentLocalImageBinding::bind)
    private val scanner: BarcodeScanner by inject()
    private val viewModel: LocalImageViewModel by viewModel()

    private val selectImageIntent = registerForActivityResult(GetContent()) {
        it?.let { handleImage(it) }
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
            binding.bindUiState(
                state = state,
                onListEmpty = { loadingDialog.dismiss() },
                onCodeDeleteClicked = { viewModel.deleteLocalDetectedCode(it) },
                onCodeItemClicked = { navigateToDetail(it) },
                onSelectImageClicked = { selectImageIntent.launch("image/*") }
            )
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

        requireActivity().apply {
            if (intent.action == Intent.ACTION_SEND) {
                if (intent.type?.startsWith("image") == true) {
                    handleIntent(intent)
                }
            }
        }
    }

    private fun navigateToDetail(codeId: Int) {
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
        findNavController().navigate(LocalImageFragmentDirections.toCodeDetailFragment(codeId, 1))
    }

    private fun handleImage(inputUri: Uri) {
        inputUri.let { uri ->
            val image = InputImage.fromFilePath(requireContext(), uri)
            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    viewModel.submitBarcodes(barcodes)
                }
                .addOnFailureListener { exception ->
                    viewModel.submitError(exception.localizedMessage ?: "")
                }
                .addOnCanceledListener {
                    viewModel.submitError("No images chosen.")
                }
        }
    }

    private fun handleIntent(intent: Intent) {
        (intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as? Uri)?.let {
            handleImage(it)
        }
        /*
        After the intent is handled, set the action to "" so it does not trigger again.
         */
        intent.action = ""
    }
}