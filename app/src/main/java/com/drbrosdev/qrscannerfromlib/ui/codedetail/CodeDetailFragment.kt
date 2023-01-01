package com.drbrosdev.qrscannerfromlib.ui.codedetail

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.text.LineBreaker
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.FrameLayout
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.isVisible
import androidx.core.view.marginBottom
import androidx.core.view.updateLayoutParams
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import coil.load
import com.drbrosdev.qrscannerfromlib.R
import com.drbrosdev.qrscannerfromlib.anims.fadeTo
import com.drbrosdev.qrscannerfromlib.databinding.FragmentCodeDetailBinding
import com.drbrosdev.qrscannerfromlib.model.QRCodeModel
import com.drbrosdev.qrscannerfromlib.ui.home.HomeTwoPaneFragmentDirections
import com.drbrosdev.qrscannerfromlib.util.QRGenUtils
import com.drbrosdev.qrscannerfromlib.util.collectFlow
import com.drbrosdev.qrscannerfromlib.util.dateAsString
import com.drbrosdev.qrscannerfromlib.util.dp
import com.drbrosdev.qrscannerfromlib.util.hideKeyboard
import com.drbrosdev.qrscannerfromlib.util.showSnackbarShort
import com.drbrosdev.qrscannerfromlib.util.updateWindowInsets
import com.drbrosdev.qrscannerfromlib.util.viewBinding
import com.google.android.material.transition.MaterialSharedAxis
import org.koin.androidx.viewmodel.ext.android.stateViewModel
import org.koin.androidx.viewmodel.scope.emptyState

class CodeDetailFragment : Fragment(R.layout.fragment_code_detail) {
    private val binding by viewBinding(FragmentCodeDetailBinding::bind)
    private val viewModel: CodeDetailViewModel by stateViewModel(
        state = { arguments ?: Bundle() }
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val fadeInAnim = AlphaAnimation(0.0f, 1.0f).apply {
            duration = 500
            startOffset = ANIM_DELAY
        }

        updateWindowInsets(binding.root)

        collectFlow(viewModel.events) {
            when (it) {
                is CodeDetailEvents.ShowThrownErrorMessage -> {
                    showSnackbarShort(it.msg, anchor = binding.buttonPerformAction)
                }
            }
        }

        collectFlow(viewModel.state) { state ->
            binding.bindUiState(
                state = state,
                onPerformAction = { handleIntent(it) },
                onDescriptionChanged = viewModel::onDescriptionChanged,
                onCopyClicked = { copyToClipboard(it) },
                onShareClicked = { toColor ->
                    val action = HomeTwoPaneFragmentDirections.toCodeShareFragment(
                        rawValue = state.code?.data?.raw ?: "",
                        colorInt = toColor
                    )
                    findNavController().navigate(action)
                }
            )
        }

        //animations
        binding.apply {
            buttonPerformAction.animate().translationY(0f).apply {
                startDelay = ANIM_DELAY * 4
            }
            linearLayoutCodeContent.apply {
                animate().scaleX(1.0f).apply { startDelay = ANIM_DELAY }
                animate().scaleY(1.0f).apply { startDelay = ANIM_DELAY }
                startAnimation(fadeInAnim)
            }
            textViewHeader.x = -1000f
            textViewHeader.animate().translationX(0f).apply {
                startDelay = ANIM_DELAY
            }
            imageViewCodeType.x = 2000f
            imageViewCodeType.animate().translationX(0f).apply {
                startDelay = ANIM_DELAY
            }

            val fromLocal = arguments?.getInt("from_local") ?: 0
            if (fromLocal == 1) {
                cardRoot.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    setMargins(leftMargin, topMargin + 32.dp, rightMargin, bottomMargin + 8.dp)
                }
            }

            imageButtonEdit.setOnClickListener {
                constraintLayout.transitionToEnd {
                    constraintLayout.setOnClickListener {
                        constraintLayout.transitionToStart().also {
                            constraintLayout.setOnClickListener(null)
                        }
                    }
                }
            }

            buttonSave.setOnClickListener {
                constraintLayout.transitionToStart().also {
                    constraintLayout.setOnClickListener(null)
                }
                hideKeyboard()
            }
        }

        /*
        Detect scrolling on the screen and hide the button if scrolling down.
        This is to make all excess content visible.
         */
        binding.apply {
            scrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
                if (scrollY > oldScrollY) {
                    //going down
                    buttonPerformAction.fadeTo(false)
                }
                if (scrollY < oldScrollY) {
                    //going up
                    buttonPerformAction.fadeTo(true)
                }
            })
            /*
            Apply justification on versions higher than O
             */
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                textViewRawData.justificationMode = LineBreaker.JUSTIFICATION_MODE_INTER_WORD
            }
        }
    }

    private fun handleIntent(content: QRCodeModel) {
        try {
            when (content) {
                is QRCodeModel.PlainModel -> { copyToClipboard(content.rawValue) }
                is QRCodeModel.UrlModel -> {
                    val urlIntent = Intent(Intent.ACTION_VIEW).apply {
                        data = if (
                            !content.link.startsWith("http://") and
                            !content.link.startsWith("https://")
                        ) {
                            Uri.parse("http://${content.link}")
                        } else {
                            Uri.parse(content.link)
                        }
                    }
                    if (activity?.packageManager != null) startActivity(urlIntent)
                }
                is QRCodeModel.SmsModel -> {
                    val smsIntent = Intent(Intent.ACTION_SENDTO).apply {
                        val number = "smsto:${content.phoneNumber}"
                        data = Uri.parse(number)
                        putExtra("sms_body", content.message)
                    }
                    if (activity?.packageManager != null) startActivity(smsIntent)
                }
                is QRCodeModel.GeoPointModel -> {
                    val geoIntent = Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse("geo:${content.lat},${content.lng}")
                    }
                    if (activity?.packageManager != null) startActivity(geoIntent)
                }
                is QRCodeModel.EmailModel -> {
                    val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:")
                        putExtra(Intent.EXTRA_EMAIL, arrayOf(content.address))
                        putExtra(Intent.EXTRA_SUBJECT, content.subject)
                        putExtra(Intent.EXTRA_TEXT, content.body)
                    }
                    if (activity?.packageManager != null) startActivity(
                        Intent.createChooser(
                            emailIntent,
                            "Choose email client"
                        )
                    )
                }
                is QRCodeModel.PhoneModel -> {
                    val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                        data = Uri.parse("tel:${content.number}")
                    }
                    if (activity?.packageManager != null) startActivity(dialIntent)
                }
                is QRCodeModel.ContactInfoModel -> {
                    //could be Address or PersonalName
                    val contactIntent = Intent(Intent.ACTION_INSERT).apply {
                        type = ContactsContract.Contacts.CONTENT_TYPE
                        putExtra(ContactsContract.Intents.Insert.NAME, content.name)
                        putExtra(ContactsContract.Intents.Insert.EMAIL, content.email)
                        putExtra(ContactsContract.Intents.Insert.PHONE, content.phone)
                    }
                    if (activity?.packageManager != null) startActivity(contactIntent)
                }
                is QRCodeModel.WifiModel -> { copyToClipboard(content.password) }
            }
        } catch (e: Exception) {
            viewModel.sendThrownError(e.localizedMessage ?: "An error has occurred.")
        }
    }

    private fun copyToClipboard(content: String) {
        val clipboardManager =
            requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("raw_data", content)
        clipboardManager.setPrimaryClip(clip)
        showSnackbarShort(
            message = "Copied to clipboard",
            anchor = binding.buttonPerformAction
        )
    }

    companion object {
        private const val ANIM_DELAY = 100L
    }
}