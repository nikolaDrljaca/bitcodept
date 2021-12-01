package com.drbrosdev.qrscannerfromlib.ui.home

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.drbrosdev.qrscannerfromlib.R
import com.drbrosdev.qrscannerfromlib.anims.fadeTo
import com.drbrosdev.qrscannerfromlib.database.QRCodeEntity
import com.drbrosdev.qrscannerfromlib.databinding.FragmentHomeBinding
import com.drbrosdev.qrscannerfromlib.ui.epoxy.createdQRCodeItem
import com.drbrosdev.qrscannerfromlib.ui.epoxy.homeModelListHeader
import com.drbrosdev.qrscannerfromlib.ui.epoxy.qRCodeListItem
import com.drbrosdev.qrscannerfromlib.util.collectFlow
import com.drbrosdev.qrscannerfromlib.util.createLoadingDialog
import com.drbrosdev.qrscannerfromlib.util.decideQrCodeColor
import com.drbrosdev.qrscannerfromlib.util.getCodeColorListAsMap
import com.drbrosdev.qrscannerfromlib.util.getColor
import com.drbrosdev.qrscannerfromlib.util.heightAsFlow
import com.drbrosdev.qrscannerfromlib.util.showSnackbarShort
import com.drbrosdev.qrscannerfromlib.util.showSnackbarShortWithAction
import com.drbrosdev.qrscannerfromlib.util.updateWindowInsets
import com.drbrosdev.qrscannerfromlib.util.viewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.transition.MaterialSharedAxis
import io.github.g00fy2.quickie.QRResult
import io.github.g00fy2.quickie.ScanCustomCode
import io.github.g00fy2.quickie.config.BarcodeFormat
import io.github.g00fy2.quickie.config.ScannerConfig
import org.koin.androidx.navigation.koinNavGraphViewModel

class HomeFragment : Fragment(R.layout.fragment_home) {
    private val viewModel by koinNavGraphViewModel<HomeViewModel>(R.id.nav_graph)
    private val binding by viewBinding(FragmentHomeBinding::bind)

    /*
    Scanner lib configuration and setup. Only support qr format, supply text above frame,
    show flashlight toggle and use haptic feedback
     */
    private val scanQrCode = registerForActivityResult(ScanCustomCode(), ::handleResult)
    private val scannerConfig = ScannerConfig.build {
        setBarcodeFormats(listOf(BarcodeFormat.FORMAT_QR_CODE))
        setOverlayStringRes(R.string.place_the_qr_code_in_the_indicated_rectangle)
        setShowTorchToggle(true)
        setHapticSuccessFeedback(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val loadingDialog = createLoadingDialog()
        /*
        Apply window insets and setup colors for statusBar and background
         */
        val statusBarColor = ContextCompat.getColor(requireContext(), R.color.background)
        requireActivity().window.statusBarColor = statusBarColor
        updateWindowInsets(binding.root)

        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }

        viewModel.emitCodeItemHeight(binding.recyclerViewCodes.heightAsFlow())

        //main state, collect data and render on screen
        collectFlow(viewModel.state) { state ->
            binding.apply {
                tvEmptyList.apply {
                    fadeTo(state.isEmpty)
                    text = getString(R.string.no_codes_yet)
                }

                progressBar.fadeTo(state.isLoading)
                recyclerViewCodes.fadeTo(!state.isEmpty)

                recyclerViewCodes.withModels {
                    state.userCodeList.forEach { code ->
                        createdQRCodeItem {
                            id(code.id)
                            onItemClicked { onItemClicked(it) }
                            onDeleteClicked { onDeleteItemClicked(it) }
                            item(code)
                            colorInt(getColor(R.color.candy_teal))
                            height(state.codeItemHeight)
                        }
                    }

                    if (!state.isEmpty)
                        homeModelListHeader { id("user_codes_header") }

                    state.codeList.forEach { code ->
                        qRCodeListItem {
                            id(code.id)
                            onItemClicked { onItemClicked(it) }
                            onDeleteClicked { onDeleteItemClicked(it) }
                            item(code)
                            colorInt(decideQrCodeColor(code))
                            height(state.codeItemHeight)
                        }
                    }

                }
            }

        }


        //collector for one-shot events fired from the viewModel
        collectFlow(viewModel.eventChannel) { event ->
            when (event) {
                is HomeEvents.ShowCurrentCodeSaved -> {
                    loadingDialog.dismiss()
                    val arg = bundleOf("code_id" to event.id)
                    findNavController().navigate(
                        R.id.action_homeFragment_to_codeDetailFragment,
                        arg
                    )
                }
                is HomeEvents.ShowUndoCodeDelete -> {
                    showSnackbarShortWithAction(
                        message = getString(R.string.code_deleted),
                        actionText = "UNDO",
                        anchor = binding.buttonLocalImageScan
                    ) { viewModel.undoDelete(event.code) }
                }
                is HomeEvents.ShowSavingCode -> {
                    loadingDialog.show()
                }
                is HomeEvents.ShowErrorCreatingCodeImage -> {
                    showSnackbarShort(
                        getString(R.string.code_could_not_be_created),
                        anchor = binding.buttonLocalImageScan
                    )
                }
                is HomeEvents.CalendarCodeEvent -> {
                    showSnackbarShort(
                        "Calendar events not supported.",
                        anchor = binding.buttonLocalImageScan
                    )
                }
                is HomeEvents.ShowSupportDialog -> {
                    findNavController().navigate(R.id.action_homeFragment_to_promptFragment)
                }
            }
        }

        //view-setup, non-state related. Animations, clickListeners
        binding.apply {
            imageButtonDeleteAll.setOnClickListener {
                showConfirmDialog(message = getString(R.string.all_codes_will_delete)) {
                    viewModel.deleteAllCodes()
                }
            }

            imageButtonInfo.setOnClickListener {
                exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
                reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
                findNavController().navigate(R.id.action_homeFragment_to_infoFragment)
            }

            buttonLocalImageScan.setOnClickListener {
                exitTransition = MaterialSharedAxis(MaterialSharedAxis.Y, true)
                reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Y, false)
                findNavController().navigate(R.id.action_homeFragment_to_localImageFragment)
            }

            buttonNewScan.setOnClickListener {
                exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
                reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
                scanQrCode.launch(scannerConfig)
            }

            imageButtonCreateCode.setOnClickListener {
                exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
                reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
                findNavController().navigate(R.id.action_homeFragment_to_createCodeFragment)
            }

            imageViewAppIcon.apply {
                val shape = shapeAppearanceModel.toBuilder()
                    .setAllCornerSizes(20f)
                    .build()
                shapeAppearanceModel = shape

            }
        }
    }

    private fun onItemClicked(code: QRCodeEntity) {
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
        val arg = bundleOf("code_id" to code.id)
        findNavController().navigate(R.id.action_homeFragment_to_codeDetailFragment, arg)
    }

    private fun onDeleteItemClicked(code: QRCodeEntity) = viewModel.deleteCode(code)

    //callback for scanner lib's activity result
    //goes further down to handle all possible cases
    private fun handleResult(result: QRResult) {
        when (result) {
            is QRResult.QRSuccess -> {
                viewModel.handleResultContent(result.content, getCodeColorListAsMap())
            }
            is QRResult.QRUserCanceled -> {
                showSnackbarShort(
                    getString(R.string.scan_cancelled),
                    anchor = binding.buttonLocalImageScan
                )
            }
            is QRResult.QRMissingPermission -> {
                showSnackbarShort(
                    getString(R.string.permission_not_granted),
                    anchor = binding.buttonLocalImageScan
                )
            }
            is QRResult.QRError -> {
                showSnackbarShort(
                    getString(R.string.error_occurred),
                    anchor = binding.buttonLocalImageScan
                )
            }
        }
    }

    //alert dialog shown when attempting to delete all codes
    private fun showConfirmDialog(
        title: String = getString(R.string.are_you_sure),
        message: String = "",
        onPositiveClick: () -> Unit
    ) {
        MaterialAlertDialogBuilder(requireContext(), R.style.ThemeOverlay_App_MaterialAlertDialog)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                onPositiveClick()
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.no)) { dialog, _ ->
                dialog.dismiss()
            }
            .create().show()
    }
}