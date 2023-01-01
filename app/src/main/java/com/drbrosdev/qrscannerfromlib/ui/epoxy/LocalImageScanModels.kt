package com.drbrosdev.qrscannerfromlib.ui.epoxy

import coil.load
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.drbrosdev.qrscannerfromlib.R
import com.drbrosdev.qrscannerfromlib.anims.fadeTo
import com.drbrosdev.qrscannerfromlib.database.QRCodeEntity
import com.drbrosdev.qrscannerfromlib.databinding.DetectCodesSelectButtonBinding
import com.drbrosdev.qrscannerfromlib.databinding.DetectedCodesHeaderBinding
import com.drbrosdev.qrscannerfromlib.databinding.DetectedCodesInfoBinding
import com.drbrosdev.qrscannerfromlib.databinding.LocalImageQrCodeListItemBinding
import com.drbrosdev.qrscannerfromlib.model.QRCodeModel
import com.drbrosdev.qrscannerfromlib.util.QRGenUtils

@EpoxyModelClass
abstract class LocalImageHeaderModel :
    ViewBindingKotlinModel<DetectedCodesHeaderBinding>(R.layout.detected_codes_header) {

    @EpoxyAttribute
    var headerText: String = ""

    override fun DetectedCodesHeaderBinding.bind() {
        textViewHeader.text = headerText
    }
}

@EpoxyModelClass
abstract class LocalImageInfoModel :
    ViewBindingKotlinModel<DetectedCodesInfoBinding>(R.layout.detected_codes_info) {

    @EpoxyAttribute
    lateinit var infoText: String

    override fun DetectedCodesInfoBinding.bind() {
        tvInfoText.text = infoText
    }
}

@EpoxyModelClass
abstract class LocalImageSelectButtonModel :
    ViewBindingKotlinModel<DetectCodesSelectButtonBinding>(R.layout.detect_codes_select_button) {

    @EpoxyAttribute
    lateinit var onClick: () -> Unit

    override fun DetectCodesSelectButtonBinding.bind() {
        buttonSelectImage.setOnClickListener {
            onClick()
        }
    }
}

@EpoxyModelClass
abstract class LocalImageCodeModel :
    ViewBindingKotlinModel<LocalImageQrCodeListItemBinding>(R.layout.local_image_qr_code_list_item) {

    @EpoxyAttribute
    lateinit var onItemClicked: (QRCodeEntity) -> Unit

    @EpoxyAttribute
    lateinit var onDeleteClicked: (QRCodeEntity) -> Unit

    @EpoxyAttribute
    lateinit var item: QRCodeEntity

    @EpoxyAttribute
    var showDeleteButton = false

    @EpoxyAttribute
    var colorInt: Int = 0

    override fun LocalImageQrCodeListItemBinding.bind() {
        val model = item.data
        card.setOnClickListener { onItemClicked(item) }
        ibDelete.setOnClickListener { onDeleteClicked(item) }
        ibDelete.fadeTo(showDeleteButton)

        val description = if (item.desc.isEmpty()) item.data.raw else item.desc

        when (model) {
            is QRCodeModel.PlainModel -> {
                tvLabel.text = "Plain Text"
                tvShortDesc.text = description
                card.setCardBackgroundColor(colorInt)
                val bmp = QRGenUtils.createCodeBitmap(model.rawValue, colorInt)
                ivCode.load(bmp)
                ivImage.load(R.drawable.ic_round_text_fields_24)
            }
            is QRCodeModel.SmsModel -> {
                tvLabel.text = "SMS"
                tvShortDesc.text = description
                card.setCardBackgroundColor(colorInt)
                ivImage.load(R.drawable.message_icon)
                val bmp = QRGenUtils.createCodeBitmap(model.rawValue, colorInt)
                ivCode.load(bmp)
            }
            is QRCodeModel.UrlModel -> {
                tvLabel.text = "Link"
                tvShortDesc.text = description
                card.setCardBackgroundColor(colorInt)
                ivImage.load(R.drawable.link_icon)
                val bmp = QRGenUtils.createCodeBitmap(model.rawValue, colorInt)
                ivCode.load(bmp)
            }
            is QRCodeModel.GeoPointModel -> {
                tvLabel.text = "Location"
                tvShortDesc.text = description
                card.setCardBackgroundColor(colorInt)
                ivImage.load(R.drawable.globe_icon)
                val bmp = QRGenUtils.createCodeBitmap(model.rawValue, colorInt)
                ivCode.load(bmp)
            }
            is QRCodeModel.ContactInfoModel -> {
                tvLabel.text = "Contact"
                tvShortDesc.text = description
                card.setCardBackgroundColor(colorInt)
                ivImage.load(R.drawable.contact_book_icon)
                val bmp = QRGenUtils.createCodeBitmap(model.rawValue, colorInt)
                ivCode.load(bmp)
            }
            is QRCodeModel.EmailModel -> {
                tvLabel.text = "Email"
                tvShortDesc.text = description
                card.setCardBackgroundColor(colorInt)
                ivImage.load(R.drawable.email_icon)
                val bmp = QRGenUtils.createCodeBitmap(model.rawValue, colorInt)
                ivCode.load(bmp)
            }
            is QRCodeModel.PhoneModel -> {
                tvLabel.text = "Phone"
                tvShortDesc.text = description
                card.setCardBackgroundColor(colorInt)
                ivImage.load(R.drawable.phone_icon)
                val bmp = QRGenUtils.createCodeBitmap(model.rawValue, colorInt)
                ivCode.load(bmp)
            }
            is QRCodeModel.WifiModel -> {
                tvLabel.text = "Wifi"
                tvShortDesc.text = description
                card.setCardBackgroundColor(colorInt)
                ivImage.load(R.drawable.ic_round_wifi_24)
                val bmp = QRGenUtils.createCodeBitmap(model.rawValue, colorInt)
                ivCode.load(bmp)
            }
        }
    }
}