package com.drbrosdev.qrscannerfromlib.ui.epoxy

import android.view.ViewGroup
import android.widget.FrameLayout
import coil.load
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.drbrosdev.qrscannerfromlib.R
import com.drbrosdev.qrscannerfromlib.database.QRCodeEntity
import com.drbrosdev.qrscannerfromlib.databinding.ModelCodeHeaderBinding
import com.drbrosdev.qrscannerfromlib.databinding.ModelCreateCodeBinding
import com.drbrosdev.qrscannerfromlib.databinding.QrCodeListItemBinding
import com.drbrosdev.qrscannerfromlib.model.QRCodeModel
import com.drbrosdev.qrscannerfromlib.util.QRGenUtils

@EpoxyModelClass
abstract class QRCodeListItemEpoxyModel :
    ViewBindingKotlinModel<QrCodeListItemBinding>(R.layout.qr_code_list_item) {
    @EpoxyAttribute
    lateinit var onItemClicked: (QRCodeEntity) -> Unit

    @EpoxyAttribute
    lateinit var onDeleteClicked: (QRCodeEntity) -> Unit

    @EpoxyAttribute
    lateinit var item: QRCodeEntity

    @EpoxyAttribute
    var colorInt: Int = 0

    @EpoxyAttribute
    var height: Int = 0

    override fun QrCodeListItemBinding.bind() {
        frameLayout.layoutParams =
            FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, height)

        val model = item.data
        card.setOnClickListener { onItemClicked(item) }
        ibDelete.setOnClickListener { onDeleteClicked(item) }

        when (model) {
            is QRCodeModel.PlainModel -> {
                tvLabel.text = "Plain Text"
                tvShortDesc.text = model.rawValue
                card.setCardBackgroundColor(colorInt)
                val bmp = QRGenUtils.createCodeBitmap(model.rawValue, colorInt)
                ivCode.load(bmp)
                ivImage.load(R.drawable.ic_round_text_fields_24)
            }
            is QRCodeModel.SmsModel -> {
                tvLabel.text = "SMS"
                tvShortDesc.text = model.phoneNumber
                card.setCardBackgroundColor(colorInt)
                ivImage.load(R.drawable.message_icon)
                val bmp = QRGenUtils.createCodeBitmap(model.rawValue, colorInt)
                ivCode.load(bmp)
            }
            is QRCodeModel.UrlModel -> {
                tvLabel.text = "Link"
                tvShortDesc.text = model.link
                card.setCardBackgroundColor(colorInt)
                ivImage.load(R.drawable.link_icon)
                val bmp = QRGenUtils.createCodeBitmap(model.rawValue, colorInt)
                ivCode.load(bmp)
            }
            is QRCodeModel.GeoPointModel -> {
                tvLabel.text = "Location"
                card.setCardBackgroundColor(colorInt)
                ivImage.load(R.drawable.globe_icon)
                val bmp = QRGenUtils.createCodeBitmap(model.rawValue, colorInt)
                ivCode.load(bmp)
            }
            is QRCodeModel.ContactInfoModel -> {
                tvLabel.text = "Contact"
                tvShortDesc.text = model.name
                card.setCardBackgroundColor(colorInt)
                ivImage.load(R.drawable.contact_book_icon)
                val bmp = QRGenUtils.createCodeBitmap(model.rawValue, colorInt)
                ivCode.load(bmp)
            }
            is QRCodeModel.EmailModel -> {
                tvLabel.text = "Email"
                tvShortDesc.text = model.address
                card.setCardBackgroundColor(colorInt)
                ivImage.load(R.drawable.email_icon)
                val bmp = QRGenUtils.createCodeBitmap(model.rawValue, colorInt)
                ivCode.load(bmp)
            }
            is QRCodeModel.PhoneModel -> {
                tvLabel.text = "Phone"
                tvShortDesc.text = model.number
                card.setCardBackgroundColor(colorInt)
                ivImage.load(R.drawable.phone_icon)
                val bmp = QRGenUtils.createCodeBitmap(model.rawValue, colorInt)
                ivCode.load(bmp)
            }
            is QRCodeModel.WifiModel -> {
                tvLabel.text = "Wifi"
                tvShortDesc.text = model.rawValue
                card.setCardBackgroundColor(colorInt)
                ivImage.load(R.drawable.ic_round_wifi_24)
                val bmp = QRGenUtils.createCodeBitmap(model.rawValue, colorInt)
                ivCode.load(bmp)
            }
        }
    }
}

@EpoxyModelClass
abstract class HomeModelListHeader :
    ViewBindingKotlinModel<ModelCodeHeaderBinding>(R.layout.model_code_header) {

    @EpoxyAttribute
    lateinit var text: String

    override fun ModelCodeHeaderBinding.bind() {
        textViewHeader.text = text
    }
}

@EpoxyModelClass
abstract class CreatedQRCodeItemEpoxyModel :
    ViewBindingKotlinModel<QrCodeListItemBinding>(R.layout.qr_code_list_item) {
    @EpoxyAttribute
    lateinit var onItemClicked: (QRCodeEntity) -> Unit

    @EpoxyAttribute
    lateinit var onDeleteClicked: (QRCodeEntity) -> Unit

    @EpoxyAttribute
    lateinit var item: QRCodeEntity

    @EpoxyAttribute
    var colorInt = 0

    @EpoxyAttribute
    var height: Int = 0

    override fun QrCodeListItemBinding.bind() {
        frameLayout.layoutParams =
            FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, height)
        card.setOnClickListener { onItemClicked(item) }
        ibDelete.setOnClickListener { onDeleteClicked(item) }
        val model = item.data
        if (model is QRCodeModel.PlainModel) {
            tvLabel.text = "Your code"
            tvShortDesc.text = model.rawValue
            card.setCardBackgroundColor(colorInt)
            val bmp = QRGenUtils.createCodeBitmap(model.rawValue, colorInt)
            ivCode.load(bmp)
            ivImage.load(R.drawable.ic_round_person_24)
        }
    }
}

@EpoxyModelClass
abstract class CreateCodeItemEpoxyModel :
    ViewBindingKotlinModel<ModelCreateCodeBinding>(R.layout.model_create_code) {
    @EpoxyAttribute
    lateinit var onItemClicked: () -> Unit

    @EpoxyAttribute
    var colorInt = 0

    @EpoxyAttribute
    var imageColor = 0

    @EpoxyAttribute
    var height: Int = 0

    override fun ModelCreateCodeBinding.bind() {
        frameLayout.layoutParams =
            FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, height)

        card.setOnClickListener { onItemClicked() }

        tvLabel.text = "Create code"
        tvShortDesc.text = "Create Your own QR code!"
        card.setCardBackgroundColor(colorInt)
        ivImage.load(R.drawable.ic_round_person_24)
        ivImage.setColorFilter(imageColor)
    }
}
