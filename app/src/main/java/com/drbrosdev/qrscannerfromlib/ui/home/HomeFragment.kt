package com.drbrosdev.qrscannerfromlib.ui.home

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.drbrosdev.qrscannerfromlib.R
import com.drbrosdev.qrscannerfromlib.database.QRCodeEntity
import com.drbrosdev.qrscannerfromlib.databinding.FragmentHomeBinding
import com.drbrosdev.qrscannerfromlib.model.QRCodeModel
import com.drbrosdev.qrscannerfromlib.network.CreateQRCodeRequest
import com.drbrosdev.qrscannerfromlib.ui.epoxy.createdQRCodeItem
import com.drbrosdev.qrscannerfromlib.ui.epoxy.homeModelListHeader
import com.drbrosdev.qrscannerfromlib.ui.epoxy.qRCodeListItem
import com.drbrosdev.qrscannerfromlib.util.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.transition.MaterialSharedAxis
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.review.testing.FakeReviewManager
import io.github.g00fy2.quickie.QRResult
import io.github.g00fy2.quickie.ScanCustomCode
import io.github.g00fy2.quickie.config.BarcodeFormat
import io.github.g00fy2.quickie.config.ScannerConfig
import io.github.g00fy2.quickie.content.QRContent
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment(R.layout.fragment_home) {
    private val viewModel by viewModel<HomeViewModel>()
    private val binding by viewBinding<FragmentHomeBinding>()

    /*
    Requester class, has an instance of httpClient which makes a call to an outside api to
    generate the image for the scanned qr code. The scanner lib does not provide bimap of image so this
    approach is taken. If there is no internet, no image is saved. The image comes in a ByteArray format.
     */
    private val requester = CreateQRCodeRequest()

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

        //collect amount of times a code was scanned -- if 5 show review flow
        collectFlow(viewModel.showReviewCount) { count ->
            if (count == 5 || count == 25) {
                launchInAppReview()
                viewModel.incrementStartupCount()
            }
        }

        //main state, collect data and render on screen
        collectStateFlow(viewModel.viewState) { state ->
            binding.apply {
                //adapter.submitList(state.codeList)
                tvEmptyList.apply {
                    isVisible = state.isEmpty
                    text = getString(R.string.no_codes_yet)
                }
                tvError.apply {
                    isVisible = state.isError
                    text = state.errorMessage
                }
                progressBar.isVisible = state.isLoading

                recyclerViewCodes.withModels {
                    state.userCodeList.forEach { code ->
                        createdQRCodeItem {
                            id(code.id)
                            onItemClicked { onItemClicked(it) }
                            onDeleteClicked { onDeleteItemClicked(it) }
                            item(code)
                            context(requireContext())
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
                            context(requireContext())
                        }
                    }
                }
            }
        }

        //collector for one-shot events fired from the viewModel
        collectEvents(viewModel.eventChannel) { event ->
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
                        anchor = binding.buttonInfo
                    ) { viewModel.undoDelete(event.code) }
                }
                is HomeEvents.ShowSavingCode -> {
                    loadingDialog.show()
                }
                is HomeEvents.ShowErrorCreatingCodeImage -> {
                    showSnackbarShort(
                        getString(R.string.code_could_not_be_created),
                        anchor = binding.buttonInfo
                    )
                }
                is HomeEvents.ShowFirstUpdateDialog -> {
                    findNavController().navigate(R.id.action_homeFragment_to_update1Fragment)
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

            buttonInfo.setOnClickListener {
                exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
                reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
                findNavController().navigate(R.id.action_homeFragment_to_infoFragment)
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
        }
    }

    //called by RV lambda when codes are clicked, navigate with transitions
    private fun onItemClicked(code: QRCodeEntity) {
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
        val arg = bundleOf("code_id" to code.id)
        findNavController().navigate(R.id.action_homeFragment_to_codeDetailFragment, arg)
    }

    //called by RV lambda when delete icon is clicked
    private fun onDeleteItemClicked(code: QRCodeEntity) = viewModel.deleteCode(code)

    //callback for scanner lib's activity result
    //goes further down to handle all possible cases
    private fun handleResult(result: QRResult) {
        when (result) {
            is QRResult.QRSuccess -> {
                handleResultContent(result.content)
                viewModel.incrementStartupCount()
            }
            is QRResult.QRUserCanceled -> {
                showSnackbarShort(getString(R.string.scan_cancelled), anchor = binding.buttonInfo)
            }
            is QRResult.QRMissingPermission -> {
                showSnackbarShort(
                    getString(R.string.permission_not_granted),
                    anchor = binding.buttonInfo
                )
            }
            is QRResult.QRError -> {
                showSnackbarShort(
                    getString(R.string.error_occurred),
                    anchor = binding.buttonInfo
                )
            }
        }
    }

    private fun handleResultContent(content: QRContent) {
        viewModel.sendSavingEvent()
        when (content) {
            is QRContent.Plain -> {
                requester.createCall(
                    codeContent = content.rawValue,
                    colorInt = getColor(R.color.candy_teal),
                    onImageLoaded = {
                        val code = QRCodeEntity(
                            data = QRCodeModel.PlainModel(content.rawValue),
                            codeImage = it
                        )
                        viewModel.insertCode(code)
                    },
                    onFail = {
                        viewModel.sendErrorImageEvent()
                        val code = QRCodeEntity(data = QRCodeModel.PlainModel(content.rawValue))
                        viewModel.insertCode(code)
                    }
                )
            }
            is QRContent.Wifi -> {
                showSnackbarShort("Wifi codes are not supported", anchor = binding.buttonInfo)
            }
            is QRContent.Url -> {
                requester.createCall(
                    codeContent = content.rawValue,
                    colorInt = getColor(R.color.candy_red),
                    onImageLoaded = {
                        val model = QRCodeEntity(
                            0, data = QRCodeModel.UrlModel(
                                rawValue = content.rawValue,
                                title = content.title,
                                link = content.url,
                            ), codeImage = it
                        )
                        viewModel.insertCode(model)
                    },
                    onFail = {
                        viewModel.sendErrorImageEvent()
                        val model = QRCodeEntity(
                            0, data = QRCodeModel.UrlModel(
                                rawValue = content.rawValue,
                                title = content.title,
                                link = content.url,
                            )
                        )
                        viewModel.insertCode(model)
                    }
                )

            }
            is QRContent.Sms -> {
                requester.createCall(
                    codeContent = content.rawValue,
                    colorInt = getColor(R.color.candy_orange),
                    onImageLoaded = {
                        val model = QRCodeEntity(
                            0, data = QRCodeModel.SmsModel(
                                rawValue = content.rawValue,
                                message = content.message,
                                phoneNumber = content.phoneNumber
                            ), codeImage = it
                        )
                        viewModel.insertCode(model)
                    },
                    onFail = {
                        viewModel.sendErrorImageEvent()
                        val model = QRCodeEntity(
                            0, data = QRCodeModel.SmsModel(
                                rawValue = content.rawValue,
                                message = content.message,
                                phoneNumber = content.phoneNumber
                            )
                        )
                        viewModel.insertCode(model)
                    }
                )
            }
            is QRContent.GeoPoint -> {
                requester.createCall(
                    codeContent = content.rawValue,
                    colorInt = getColor(R.color.candy_purple),
                    onImageLoaded = {
                        val code = QRCodeEntity(
                            data = QRCodeModel.GeoPointModel(
                                rawValue = content.rawValue,
                                lat = content.lat,
                                lng = content.lng
                            ), codeImage = it
                        )
                        viewModel.insertCode(code)
                    },
                    onFail = {
                        viewModel.sendErrorImageEvent()
                        val code = QRCodeEntity(
                            data = QRCodeModel.GeoPointModel(
                                rawValue = content.rawValue,
                                lat = content.lat,
                                lng = content.lng
                            )
                        )
                        viewModel.insertCode(code)
                    }
                )

            }
            is QRContent.Email -> {
                requester.createCall(
                    codeContent = content.rawValue,
                    colorInt = getColor(R.color.candy_blue),
                    onImageLoaded = {
                        val code = QRCodeEntity(
                            data = QRCodeModel.EmailModel(
                                rawValue = content.rawValue,
                                address = content.address,
                                body = content.body,
                                subject = content.subject
                            ), codeImage = it
                        )
                        viewModel.insertCode(code)
                    },
                    onFail = {
                        viewModel.sendErrorImageEvent()
                        val code = QRCodeEntity(
                            data = QRCodeModel.EmailModel(
                                rawValue = content.rawValue,
                                address = content.address,
                                body = content.body,
                                subject = content.subject
                            )
                        )
                        viewModel.insertCode(code)
                    }
                )

            }
            is QRContent.Phone -> {
                requester.createCall(
                    codeContent = content.rawValue,
                    colorInt = getColor(R.color.candy_green),
                    onImageLoaded = {
                        val code = QRCodeEntity(
                            data = QRCodeModel.PhoneModel(
                                rawValue = content.rawValue,
                                number = content.number
                            ), codeImage = it
                        )
                        viewModel.insertCode(code)
                    },
                    onFail = {
                        viewModel.sendErrorImageEvent()
                        val code = QRCodeEntity(
                            data = QRCodeModel.PhoneModel(
                                rawValue = content.rawValue,
                                number = content.number
                            )
                        )
                        viewModel.insertCode(code)
                    }
                )

            }
            is QRContent.ContactInfo -> {
                requester.createCall(
                    codeContent = content.rawValue,
                    colorInt = getColor(R.color.candy_yellow),
                    onImageLoaded = {
                        val code = QRCodeEntity(
                            data = QRCodeModel.ContactInfoModel(
                                rawValue = content.rawValue,
                                name = content.name.formattedName,
                                email = content.emails.firstOrNull()?.address ?: " ",
                                phone = content.phones.firstOrNull()?.number ?: " "
                            ), codeImage = it
                        )
                        viewModel.insertCode(code)
                    },
                    onFail = {
                        viewModel.sendErrorImageEvent()
                        val code = QRCodeEntity(
                            data = QRCodeModel.ContactInfoModel(
                                rawValue = content.rawValue,
                                name = content.name.formattedName,
                                email = content.emails.firstOrNull()?.address ?: " ",
                                phone = content.phones.firstOrNull()?.number ?: " "
                            )
                        )
                        viewModel.insertCode(code)
                    }
                )

            }
            is QRContent.CalendarEvent -> {
                showSnackbarShort("Calendar events not supported.", anchor = binding.buttonInfo)
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

    //in-app review flow
    private fun launchInAppReview() {
        val reviewManager = ReviewManagerFactory.create(requireActivity())
        val test = FakeReviewManager(requireActivity())
        val requestFlow = reviewManager.requestReviewFlow()

        requestFlow.addOnCompleteListener { request ->
            if (request.isSuccessful) {
                val reviewInfo = request.result
                val flow = reviewManager.launchReviewFlow(requireActivity(), reviewInfo)
                flow.addOnCompleteListener {
                    /*
                    The review flow is now complete. We have no idea if the user reviewed or
                    how the user reviewed so we just continue app flow.
                     */
                    //showShortToast("review flow finished")
                }
            }
        }
    }
}