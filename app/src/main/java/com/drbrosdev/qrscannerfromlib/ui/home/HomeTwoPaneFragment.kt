package com.drbrosdev.qrscannerfromlib.ui.home

import android.content.res.Configuration
import android.os.Build.VERSION_CODES.P
import android.os.Bundle
import android.os.Parcelable
import android.text.TextUtils.replace
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updateMargins
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.AbstractListDetailFragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.slidingpanelayout.widget.SlidingPaneLayout
import com.airbnb.epoxy.EpoxyRecyclerView
import com.drbrosdev.qrscannerfromlib.R
import com.drbrosdev.qrscannerfromlib.anims.fadeTo
import com.drbrosdev.qrscannerfromlib.billing.BillingClientWrapper
import com.drbrosdev.qrscannerfromlib.billing.PurchaseResult
import com.drbrosdev.qrscannerfromlib.databinding.FragmentHomeTwoPaneBinding
import com.drbrosdev.qrscannerfromlib.ui.codedetail.CodeDetailFragment
import com.drbrosdev.qrscannerfromlib.ui.epoxy.actionButtons
import com.drbrosdev.qrscannerfromlib.ui.epoxy.bitcodeptHeader
import com.drbrosdev.qrscannerfromlib.ui.epoxy.createCodeItem
import com.drbrosdev.qrscannerfromlib.ui.epoxy.localImageCode
import com.drbrosdev.qrscannerfromlib.ui.epoxy.localImageHeader
import com.drbrosdev.qrscannerfromlib.ui.epoxy.localImageInfo
import com.drbrosdev.qrscannerfromlib.ui.epoxy.spacer
import com.drbrosdev.qrscannerfromlib.ui.localimage.context
import com.drbrosdev.qrscannerfromlib.util.TwoPaneOnBackPressedCallback
import com.drbrosdev.qrscannerfromlib.util.WindowSizeClass
import com.drbrosdev.qrscannerfromlib.util.collectFlow
import com.drbrosdev.qrscannerfromlib.util.createLoadingDialog
import com.drbrosdev.qrscannerfromlib.util.decideQrCodeColor
import com.drbrosdev.qrscannerfromlib.util.dp
import com.drbrosdev.qrscannerfromlib.util.showConfirmDialog
import com.drbrosdev.qrscannerfromlib.util.showSnackbarShort
import com.drbrosdev.qrscannerfromlib.util.showSnackbarShortWithAction
import com.drbrosdev.qrscannerfromlib.util.updateWindowInsets
import com.drbrosdev.qrscannerfromlib.util.viewBinding
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.transition.MaterialSharedAxis
import io.github.g00fy2.quickie.QRResult
import io.github.g00fy2.quickie.ScanCustomCode
import io.github.g00fy2.quickie.config.BarcodeFormat
import io.github.g00fy2.quickie.config.ScannerConfig
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.fragment.android.replace
import org.koin.androidx.navigation.koinNavGraphViewModel

class HomeTwoPaneFragment : Fragment(R.layout.fragment_home_two_pane) {
    private val viewModel: HomeViewModel by koinNavGraphViewModel(R.id.nav_graph)
    private val binding: FragmentHomeTwoPaneBinding by viewBinding(FragmentHomeTwoPaneBinding::bind)
    private val billingWrapper by inject<BillingClientWrapper>()

    private val windowSizeClass = MutableStateFlow(WindowSizeClass.COMPACT)

    private val scanQrCode = registerForActivityResult(ScanCustomCode(), ::handleResult)
    private val scannerConfig = ScannerConfig.build {
        setBarcodeFormats(listOf(BarcodeFormat.FORMAT_QR_CODE))
        setOverlayStringRes(R.string.place_the_qr_code_in_the_indicated_rectangle)
        setShowTorchToggle(true)
        setHapticSuccessFeedback(true)
    }

    override fun onResume() {
        super.onResume()
        billingWrapper.retryToConsumePurchases()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val loadingDialog = createLoadingDialog()
        updateWindowInsets(binding.root)
        windowSizeClass.update { WindowSizeClass.computeWindowSizeClass(requireActivity()) }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            TwoPaneOnBackPressedCallback(binding.slidingPaneLayout)
        )

        binding.frameLayoutRoot.addView(object : View(requireContext()) {
            override fun onConfigurationChanged(newConfig: Configuration?) {
                super.onConfigurationChanged(newConfig)
                windowSizeClass.update { WindowSizeClass.computeWindowSizeClass(requireActivity()) }
            }
        })

        when (windowSizeClass.value) {
            WindowSizeClass.COMPACT -> binding.setupUiForCompact()
            else -> binding.setupUiForExpanded()
        }

        collectFlow(billingWrapper.purchaseFlow) {
            when (it) {
                is PurchaseResult.Failure -> Unit
                is PurchaseResult.Success -> {
                    val arg = bundleOf("home" to 1)
                    findNavController().navigate(R.id.to_gratitudeFragment, arg)
                }
            }
        }

        collectFlow(viewModel.state) { state ->
            binding.bindUiState(
                state = state,
                headerTextVisibility = windowSizeClass.value == WindowSizeClass.COMPACT,
                onCreateCodeClicked = { navigateToCreateCode() },
                onCodeItemClicked = { openDetails(it.id) },
                onCodeDelete = { viewModel.deleteCode(it) },
                onLocalScanClicked = { launchLocalScanner() },
                onCameraScanClicked = { launchCameraScanner() },
                onDeleteAll = { deleteAllCodes() },
                onInfoClicked = { navigateToInfo() }
            )
        }

        collectFlow(viewModel.eventChannel) { event ->
            when (event) {
                is HomeEvents.ShowCurrentCodeSaved -> {
                    loadingDialog.dismiss()
                    val action = HomeTwoPaneFragmentDirections.toCodeDetailFragment(event.id)
                    findNavController().navigate(action)
                }
                is HomeEvents.ShowUndoCodeDelete -> {
                    showSnackbarShortWithAction(
                        message = getString(R.string.code_deleted),
                        actionText = "UNDO",
                    ) { viewModel.undoDelete(event.code) }
                }
                is HomeEvents.ShowSavingCode -> {
                    loadingDialog.show()
                }
                is HomeEvents.ShowErrorCreatingCodeImage -> {
                    showSnackbarShort(getString(R.string.code_could_not_be_created))
                }
                is HomeEvents.CalendarCodeEvent -> {
                    showSnackbarShort("Calendar events not supported.")
                }
                is HomeEvents.ShowSupportDialog -> {
                    findNavController().navigate(HomeTwoPaneFragmentDirections.toPromptFragment())
                }
            }
        }

        binding.apply {
            imageViewAppIcon.apply {
                val shape = shapeAppearanceModel.toBuilder()
                    .setAllCornerSizes(20f)
                    .build()
                shapeAppearanceModel = shape
            }

            imageButtonInfo.setOnClickListener { navigateToInfo() }
            imageButtonScanCamera.setOnClickListener { launchCameraScanner() }
            imageButtonScanLocal.setOnClickListener { launchLocalScanner() }
            imageButtonDeleteAll.setOnClickListener { deleteAllCodes() }
        }
    }

    private fun navigateToCreateCode() {
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
        findNavController().navigate(HomeTwoPaneFragmentDirections.toCreateCodeFragment())
    }

    private fun deleteAllCodes() {
        showConfirmDialog(message = getString(R.string.all_codes_will_delete)) {
            viewModel.deleteAllCodes()
        }
    }

    private fun navigateToInfo() {
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
        findNavController().navigate(HomeTwoPaneFragmentDirections.toInfoFragment())
    }

    private fun launchCameraScanner() {
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
        scanQrCode.launch(scannerConfig)
    }

    private fun launchLocalScanner() {
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Y, true)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Y, false)
        findNavController().navigate(HomeTwoPaneFragmentDirections.toLocalImageFragment())
    }

    private fun openDetails(codeId: Int) {
        childFragmentManager.commit {
            setReorderingAllowed(true)
            replace<CodeDetailFragment>(
                R.id.detail_container,
                bundleOf("code_id" to codeId)
            )
            // If we're already open and the detail pane is visible,
            // crossfade between the fragments.
            if (binding.slidingPaneLayout.isOpen) {
                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            }
        }
        binding.slidingPaneLayout.open()
    }

    private fun handleResult(result: QRResult) {
        when (result) {
            is QRResult.QRSuccess -> {
                viewModel.handleResultContent(result.content)
            }
            is QRResult.QRUserCanceled -> {
                showSnackbarShort(getString(R.string.scan_cancelled))
            }
            is QRResult.QRMissingPermission -> {
                showSnackbarShort(getString(R.string.permission_not_granted))
            }
            is QRResult.QRError -> {
                showSnackbarShort(getString(R.string.error_occurred))
            }
        }
    }
}