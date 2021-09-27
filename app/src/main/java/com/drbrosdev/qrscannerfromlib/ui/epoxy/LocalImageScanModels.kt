package com.drbrosdev.qrscannerfromlib.ui.epoxy

import android.graphics.BitmapFactory
import coil.load
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.drbrosdev.qrscannerfromlib.R
import com.drbrosdev.qrscannerfromlib.database.QRCodeEntity
import com.drbrosdev.qrscannerfromlib.databinding.*
import com.drbrosdev.qrscannerfromlib.model.QRCodeModel

@EpoxyModelClass
abstract class LocalImageHeaderModel :
    ViewBindingKotlinModel<DetectedCodesHeaderBinding>(R.layout.detected_codes_header) {

    override fun DetectedCodesHeaderBinding.bind() {
        //TODO("Not yet implemented")
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
    var colorInt: Int = 0

    override fun LocalImageQrCodeListItemBinding.bind() {
        val bmp = BitmapFactory.decodeByteArray(
            item.codeImage ?: ByteArray(0),
            0,
            item.codeImage?.size ?: 0
        )
        val model = item.data
        card.setOnClickListener { onItemClicked(item) }
        ibDelete.setOnClickListener { onDeleteClicked(item) }

        when (model) {
            is QRCodeModel.PlainModel -> {
                tvLabel.text = "Plain Text"
                tvShortDesc.text = model.rawValue
                card.setCardBackgroundColor(colorInt)
                item.codeImage?.let { ivCode.load(bmp) }
                //ivCode.load(bmp)
                ivImage.load(R.drawable.ic_round_text_fields_24)
            }
            is QRCodeModel.SmsModel -> {
                tvLabel.text = "SMS"
                tvShortDesc.text = model.phoneNumber
                card.setCardBackgroundColor(colorInt)
                ivImage.load(R.drawable.message_icon)
                ivCode.load(bmp)
            }
            is QRCodeModel.UrlModel -> {
                tvLabel.text = "Link"
                tvShortDesc.text = model.link
                card.setCardBackgroundColor(colorInt)
                ivImage.load(R.drawable.link_icon)
                ivCode.load(bmp)
            }
            is QRCodeModel.GeoPointModel -> {
                tvLabel.text = "Location"
                card.setCardBackgroundColor(colorInt)
                ivImage.load(R.drawable.globe_icon)
                ivCode.load(bmp)
            }
            is QRCodeModel.ContactInfoModel -> {
                tvLabel.text = "Contact"
                tvShortDesc.text = model.name
                card.setCardBackgroundColor(colorInt)
                ivImage.load(R.drawable.contact_book_icon)
                ivCode.load(bmp)
            }
            is QRCodeModel.EmailModel -> {
                tvLabel.text = "Email"
                tvShortDesc.text = model.address
                card.setCardBackgroundColor(colorInt)
                ivImage.load(R.drawable.email_icon)
                ivCode.load(bmp)
            }
            is QRCodeModel.PhoneModel -> {
                tvLabel.text = "Phone"
                tvShortDesc.text = model.number
                card.setCardBackgroundColor(colorInt)
                ivImage.load(R.drawable.phone_icon)
                ivCode.load(bmp)
            }
        }
    }
}