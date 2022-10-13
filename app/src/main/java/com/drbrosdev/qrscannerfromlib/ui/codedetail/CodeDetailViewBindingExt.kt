package com.drbrosdev.qrscannerfromlib.ui.codedetail

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.res.ColorStateList
import androidx.core.view.isVisible
import coil.load
import com.drbrosdev.qrscannerfromlib.R
import com.drbrosdev.qrscannerfromlib.databinding.FragmentCodeDetailBinding
import com.drbrosdev.qrscannerfromlib.databinding.FragmentLocalImageBinding
import com.drbrosdev.qrscannerfromlib.model.QRCodeModel
import com.drbrosdev.qrscannerfromlib.util.QRGenUtils
import com.drbrosdev.qrscannerfromlib.util.dateAsString
import com.drbrosdev.qrscannerfromlib.util.showSnackbarShort

val FragmentCodeDetailBinding.context: Context
    get() = root.context

fun FragmentCodeDetailBinding.bindUiState(
    state: CodeDetailUiModel,
    onPerformAction: (QRCodeModel) -> Unit,
    onBindColor: (Int) -> Unit,
    onCopyClicked: (String) -> Unit,
    onShareClicked: (String) -> Unit
) {
    progressBar.isVisible = state.isLoading

    state.code?.let { code ->
        when (code.data) {
            is QRCodeModel.PlainModel -> {
                val colorInt = context.getColor(R.color.candy_teal)
                cardRoot.setCardBackgroundColor(colorInt)
                onBindColor(colorInt)
                textViewCodeHeader.text = code.data.rawValue
                textViewType.text = "Plain text"
                textViewDate.text = dateAsString(code.time)
                val bmp = QRGenUtils.createCodeBitmap(code.data.rawValue, colorInt)
                imageViewQrCode.load(bmp)
                imageViewCodeType.load(R.drawable.ic_round_text_fields_24)
                textViewRawData.text = code.data.rawValue
                buttonPerformAction.apply {
                    text = "Copy to clipboard"
                    setOnClickListener {
                        onPerformAction(code.data)
                    }
                }
            }
            is QRCodeModel.SmsModel -> {
                val colorInt = context.getColor(R.color.candy_orange)
                cardRoot.setCardBackgroundColor(colorInt)
                onBindColor(colorInt)
                imageViewCodeType.load(R.drawable.message_icon)
                textViewCodeHeader.text = code.data.phoneNumber
                textViewType.text = "SMS"
                textViewDate.text = dateAsString(code.time)
                val bmp = QRGenUtils.createCodeBitmap(code.data.rawValue, colorInt)
                imageViewQrCode.load(bmp)
                textViewRawData.text = code.data.rawValue
                buttonPerformAction.apply {
                    setOnClickListener { onPerformAction(code.data) }
                    text = "Send Sms"
                }
            }
            is QRCodeModel.UrlModel -> {
                val colorInt = context.getColor(R.color.candy_red)
                cardRoot.setCardBackgroundColor(colorInt)
                onBindColor(colorInt)
                imageViewCodeType.load(R.drawable.link_icon)
                textViewCodeHeader.text = code.data.link
                textViewType.text = "Link"
                textViewDate.text = dateAsString(code.time)
                val bmp = QRGenUtils.createCodeBitmap(code.data.rawValue, colorInt)
                imageViewQrCode.load(bmp)
                textViewRawData.text = code.data.rawValue
                buttonPerformAction.apply {
                    setOnClickListener { onPerformAction(code.data) }
                    text = "Open Link"
                }
            }
            is QRCodeModel.ContactInfoModel -> {
                val colorInt = context.getColor(R.color.candy_yellow)
                cardRoot.setCardBackgroundColor(colorInt)
                onBindColor(colorInt)
                imageViewCodeType.load(R.drawable.contact_book_icon)
                textViewCodeHeader.text = code.data.name
                textViewType.text = "Contact"
                textViewDate.text = dateAsString(code.time)
                val bmp = QRGenUtils.createCodeBitmap(code.data.rawValue, colorInt)
                imageViewQrCode.load(bmp)
                textViewRawData.text = code.data.rawValue
                buttonPerformAction.apply {
                    setOnClickListener { onPerformAction(code.data) }
                    text = "Save Contact"
                }
            }
            is QRCodeModel.GeoPointModel -> {
                val colorInt = context.getColor(R.color.candy_purple)
                cardRoot.setCardBackgroundColor(colorInt)
                onBindColor(colorInt)
                imageViewCodeType.load(R.drawable.globe_icon)
                textViewCodeHeader.text = "Geo Point"
                textViewType.text = "Location"
                textViewDate.text = dateAsString(code.time)
                val bmp = QRGenUtils.createCodeBitmap(code.data.rawValue, colorInt)
                imageViewQrCode.load(bmp)
                textViewRawData.text = code.data.rawValue
                buttonPerformAction.apply {
                    setOnClickListener { onPerformAction(code.data) }
                    text = "Open in maps"
                }
            }
            is QRCodeModel.EmailModel -> {
                val colorInt = context.getColor(R.color.candy_blue)
                cardRoot.setCardBackgroundColor(colorInt)
                onBindColor(colorInt)
                imageViewCodeType.load(R.drawable.email_icon)
                textViewCodeHeader.text = code.data.address
                textViewType.text = "Email"
                textViewDate.text = dateAsString(code.time)
                val bmp = QRGenUtils.createCodeBitmap(code.data.rawValue, colorInt)
                imageViewQrCode.load(bmp)
                textViewRawData.text = code.data.rawValue
                buttonPerformAction.apply {
                    setOnClickListener { onPerformAction(code.data) }
                    text = "Send Email"
                }
            }
            is QRCodeModel.PhoneModel -> {
                val colorInt = context.getColor(R.color.candy_green)
                cardRoot.setCardBackgroundColor(colorInt)
                onBindColor(colorInt)
                imageViewCodeType.load(R.drawable.phone_icon)
                textViewCodeHeader.text = code.data.number
                textViewType.text = "Phone"
                textViewDate.text = dateAsString(code.time)
                val bmp = QRGenUtils.createCodeBitmap(code.data.rawValue, colorInt)
                imageViewQrCode.load(bmp)
                textViewRawData.text = code.data.rawValue
                buttonPerformAction.apply {
                    setOnClickListener { onPerformAction(code.data) }
                    text = "Dial"
                }
            }
            is QRCodeModel.WifiModel -> {
                val colorInt = context.getColor(R.color.candy_mandarin)
                cardRoot.setCardBackgroundColor(colorInt)
                onBindColor(colorInt)
                imageViewCodeType.load(R.drawable.ic_round_wifi_24)
                textViewCodeHeader.text = code.data.ssid
                textViewType.text = "Wifi"
                textViewDate.text = dateAsString(code.time)
                val bmp = QRGenUtils.createCodeBitmap(code.data.rawValue, colorInt)
                imageViewQrCode.load(bmp)
                textViewRawData.text =
                    "Network name: ${code.data.ssid}\nPassword: ${code.data.password}"
                buttonPerformAction.apply {
                    setOnClickListener {
                        onPerformAction(code.data)
                    }
                    text = "Copy password"
                }
            }
        }

        buttonCopy.setOnClickListener { onCopyClicked(code.data.raw) }
        buttonShare.setOnClickListener { onShareClicked(code.data.raw) }
    }
}