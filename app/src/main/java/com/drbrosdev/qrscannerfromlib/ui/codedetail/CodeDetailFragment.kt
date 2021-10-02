package com.drbrosdev.qrscannerfromlib.ui.codedetail

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.text.LineBreaker
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import coil.load
import com.drbrosdev.qrscannerfromlib.R
import com.drbrosdev.qrscannerfromlib.anims.fadeTo
import com.drbrosdev.qrscannerfromlib.databinding.FragmentCodeDetailBinding
import com.drbrosdev.qrscannerfromlib.model.QRCodeModel
import com.drbrosdev.qrscannerfromlib.util.*
import com.google.android.material.transition.MaterialSharedAxis
import org.koin.androidx.viewmodel.ext.android.stateViewModel

class CodeDetailFragment : Fragment(R.layout.fragment_code_detail) {
    private val binding by viewBinding(FragmentCodeDetailBinding::bind)
    private val viewModel: CodeDetailViewModel by stateViewModel(state = { requireArguments() })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val fadeInAnim = AlphaAnimation(0.0f, 1.0f).apply {
            duration = 500
            startOffset = ANIM_DELAY
        }

        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)

        //updates the padding depending on which navigation system is on the phone
        // 3-button or gesture
        //THIS IS NEEDED IN EVERY FRAGMENT
        updateWindowInsets(binding.root)

        collectFlow(viewModel.events) {
            when(it) {
                is CodeDetailEvents.ShowThrownErrorMessage -> {
                    showSnackbarShort(it.msg, anchor = binding.buttonPerformAction)
                }
            }
        }

        collectStateFlow(viewModel.viewState) { state ->
            binding.apply {
                progressBar.isVisible = state.isLoading

                state.code?.let { code ->
                    val byteArray = code.codeImage ?: ByteArray(0)
                    val bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
                    when (code.data) {
                        is QRCodeModel.PlainModel -> {
                            val colorInt = getColor(R.color.candy_teal)
                            coordinatorLayout.setBackgroundColor(colorInt)
                            requireActivity().window.statusBarColor = colorInt
                            textViewCodeHeader.text = code.data.rawValue
                            textViewType.text = "Plain text"
                            textViewDate.text = dateAsString(code.time)
                            if (code.codeImage != null) imageViewQrCode.load(bmp)
                            imageViewCodeType.load(R.drawable.ic_round_text_fields_24)
                            textViewRawData.text = code.data.rawValue
                            buttonPerformAction.apply {
                                text = "Copy to clipboard"
                                setOnClickListener {
                                    val clipboardManager =
                                        requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("raw_data", code.data.rawValue)
                                    clipboardManager.setPrimaryClip(clip)
                                    showSnackbarShort(
                                        message = "Copied to clipboard",
                                        anchor = binding.buttonPerformAction
                                    )
                                }
                            }
                        }
                        is QRCodeModel.SmsModel -> {
                            val colorInt = getColor(R.color.candy_orange)
                            coordinatorLayout.setBackgroundColor(colorInt)
                            requireActivity().window.statusBarColor = colorInt
                            imageViewCodeType.load(R.drawable.message_icon)
                            textViewCodeHeader.text = code.data.phoneNumber
                            textViewType.text = "SMS"
                            textViewDate.text = dateAsString(code.time)
                            if (code.codeImage != null) imageViewQrCode.load(bmp)
                            textViewRawData.text = code.data.rawValue
                            buttonPerformAction.apply {
                                setOnClickListener { handleIntent(code.data) }
                                text = "Send Sms"
                            }
                        }
                        is QRCodeModel.UrlModel -> {
                            val colorInt = getColor(R.color.candy_red)
                            coordinatorLayout.setBackgroundColor(colorInt)
                            requireActivity().window.statusBarColor = colorInt
                            imageViewCodeType.load(R.drawable.link_icon)
                            textViewCodeHeader.text = code.data.title
                            textViewType.text = "Link"
                            textViewDate.text = dateAsString(code.time)
                            if (code.codeImage != null) imageViewQrCode.load(bmp)
                            textViewRawData.text = code.data.rawValue
                            buttonPerformAction.apply {
                                setOnClickListener { handleIntent(code.data) }
                                text = "Open Link"
                            }
                        }
                        is QRCodeModel.ContactInfoModel -> {
                            val colorInt = getColor(R.color.candy_yellow)
                            coordinatorLayout.setBackgroundColor(colorInt)
                            requireActivity().window.statusBarColor = colorInt
                            imageViewCodeType.load(R.drawable.contact_book_icon)
                            textViewCodeHeader.text = code.data.name
                            textViewType.text = "Contact"
                            textViewDate.text = dateAsString(code.time)
                            if (code.codeImage != null) imageViewQrCode.load(bmp)
                            textViewRawData.text = code.data.rawValue
                            buttonPerformAction.apply {
                                setOnClickListener { handleIntent(code.data) }
                                text = "Save Contact"
                            }
                        }
                        is QRCodeModel.GeoPointModel -> {
                            val colorInt = getColor(R.color.candy_purple)
                            coordinatorLayout.setBackgroundColor(colorInt)
                            requireActivity().window.statusBarColor = colorInt
                            imageViewCodeType.load(R.drawable.globe_icon)
                            textViewCodeHeader.text = "Geo Point"
                            textViewType.text = "Location"
                            textViewDate.text = dateAsString(code.time)
                            if (code.codeImage != null) imageViewQrCode.load(bmp)
                            textViewRawData.text = code.data.rawValue
                            buttonPerformAction.apply {
                                setOnClickListener { handleIntent(code.data) }
                                text = "Open in maps"
                            }
                        }
                        is QRCodeModel.EmailModel -> {
                            val colorInt = getColor(R.color.candy_blue)
                            coordinatorLayout.setBackgroundColor(colorInt)
                            requireActivity().window.statusBarColor = colorInt
                            imageViewCodeType.load(R.drawable.email_icon)
                            textViewCodeHeader.text = code.data.address
                            textViewType.text = "Email"
                            textViewDate.text = dateAsString(code.time)
                            if (code.codeImage != null) imageViewQrCode.load(bmp)
                            textViewRawData.text = code.data.rawValue
                            buttonPerformAction.apply {
                                setOnClickListener { handleIntent(code.data) }
                                text = "Send Email"
                            }
                        }
                        is QRCodeModel.PhoneModel -> {
                            val colorInt = getColor(R.color.candy_green)
                            coordinatorLayout.setBackgroundColor(colorInt)
                            requireActivity().window.statusBarColor = colorInt
                            imageViewCodeType.load(R.drawable.phone_icon)
                            textViewCodeHeader.text = code.data.number
                            textViewType.text = "Phone"
                            textViewDate.text = dateAsString(code.time)
                            if (code.codeImage != null) imageViewQrCode.load(bmp)
                            textViewRawData.text = code.data.rawValue
                            buttonPerformAction.apply {
                                setOnClickListener { handleIntent(code.data) }
                                text = "Dial"
                            }
                        }
                        is QRCodeModel.WifiModel -> {
                            val colorInt = getColor(R.color.candy_mandarin)
                            coordinatorLayout.setBackgroundColor(colorInt)
                            requireActivity().window.statusBarColor = colorInt
                            imageViewCodeType.load(R.drawable.ic_round_wifi_24)
                            textViewCodeHeader.text = code.data.ssid
                            textViewType.text = "Wifi"
                            textViewDate.text = dateAsString(code.time)
                            if (code.codeImage != null) imageViewQrCode.load(bmp)
                            textViewRawData.text = "Network name: ${code.data.ssid}\nPassword: ${code.data.password}"
                            buttonPerformAction.apply {
                                setOnClickListener {
                                    val clipboardManager =
                                        requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("raw_data", code.data.password)
                                    clipboardManager.setPrimaryClip(clip)
                                    showSnackbarShort(
                                        message = "Copied to clipboard",
                                        anchor = binding.buttonPerformAction
                                    )
                                }
                                text = "Copy password"
                            }
                        }
                    }
                }
            }
        }

        //animations
        binding.apply {
            buttonPerformAction.startAnimation(fadeInAnim)
            linearLayoutCodeContent.startAnimation(fadeInAnim)
            textViewHeader.x = -1000f
            textViewHeader.animate().translationX(0f).apply {
                startDelay = ANIM_DELAY
            }
            imageViewCodeType.x = 2000f
            imageViewCodeType.animate().translationX(0f).apply {
                startDelay = ANIM_DELAY
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
                is QRCodeModel.PlainModel -> Unit
                is QRCodeModel.UrlModel -> {
                    val urlIntent = Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse(content.link)
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
                is QRCodeModel.WifiModel -> Unit
            }
        } catch (e: Exception) {
            viewModel.sendThrownError(e.localizedMessage ?: "An error has occurred.")
        }
    }

    companion object {
        private const val ANIM_DELAY = 100L
    }
}