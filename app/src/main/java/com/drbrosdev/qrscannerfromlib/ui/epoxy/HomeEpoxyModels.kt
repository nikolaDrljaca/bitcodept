package com.drbrosdev.qrscannerfromlib.ui.epoxy

import android.content.Context
import android.graphics.BitmapFactory
import androidx.core.content.ContextCompat
import coil.load
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.drbrosdev.qrscannerfromlib.R
import com.drbrosdev.qrscannerfromlib.database.QRCodeEntity
import com.drbrosdev.qrscannerfromlib.databinding.ModelCodeHeaderBinding
import com.drbrosdev.qrscannerfromlib.databinding.QrCodeListItemBinding
import com.drbrosdev.qrscannerfromlib.model.QRCodeModel

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
    lateinit var context: Context

    override fun QrCodeListItemBinding.bind() {
        val bmp = BitmapFactory.decodeByteArray(item.codeImage ?: ByteArray(0), 0, item.codeImage?.size ?: 0)
        val model = item.data
        card.setOnClickListener { onItemClicked(item) }
        ibDelete.setOnClickListener { onDeleteClicked(item) }

        when (model) {
            is QRCodeModel.PlainModel -> {
                val colorInt = ContextCompat.getColor(context, R.color.candy_teal)
                tvLabel.text = "Plain Text"
                tvShortDesc.text = model.rawValue
                card.setCardBackgroundColor(colorInt)
                ivCode.load(bmp)
                ivImage.load(R.drawable.ic_round_text_fields_24)
            }
            is QRCodeModel.SmsModel -> {
                val colorInt = ContextCompat.getColor(context, R.color.candy_orange)
                tvLabel.text = "SMS"
                tvShortDesc.text = model.phoneNumber
                card.setCardBackgroundColor(colorInt)
                ivImage.load(R.drawable.message_icon)
                ivCode.load(bmp)
            }
            is QRCodeModel.UrlModel -> {
                val colorInt = ContextCompat.getColor(context, R.color.candy_red)
                tvLabel.text = "Link"
                tvShortDesc.text = model.link
                card.setCardBackgroundColor(colorInt)
                ivImage.load(R.drawable.link_icon)
                ivCode.load(bmp)
            }
            is QRCodeModel.GeoPointModel -> {
                val colorInt = ContextCompat.getColor(context, R.color.candy_purple)
                tvLabel.text = "Location"
                card.setCardBackgroundColor(colorInt)
                ivImage.load(R.drawable.globe_icon)
                ivCode.load(bmp)
            }
            is QRCodeModel.ContactInfoModel -> {
                val colorInt = ContextCompat.getColor(context, R.color.candy_yellow)
                tvLabel.text = "Contact"
                tvShortDesc.text = model.name
                card.setCardBackgroundColor(colorInt)
                ivImage.load(R.drawable.contact_book_icon)
                ivCode.load(bmp)
            }
            is QRCodeModel.EmailModel -> {
                val colorInt = ContextCompat.getColor(context, R.color.candy_blue)
                tvLabel.text = "Email"
                tvShortDesc.text = model.address
                card.setCardBackgroundColor(colorInt)
                ivImage.load(R.drawable.email_icon)
                ivCode.load(bmp)
            }
            is QRCodeModel.PhoneModel -> {
                val colorInt = ContextCompat.getColor(context, R.color.candy_green)
                tvLabel.text = "Phone"
                tvShortDesc.text = model.number
                card.setCardBackgroundColor(colorInt)
                ivImage.load(R.drawable.phone_icon)
                ivCode.load(bmp)
            }
        }
    }
}

@EpoxyModelClass
abstract class HomeModelListHeader :
    ViewBindingKotlinModel<ModelCodeHeaderBinding>(R.layout.model_code_header) {
    override fun ModelCodeHeaderBinding.bind() {
        //TODO("Not yet implemented")
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
    lateinit var context: Context

    override fun QrCodeListItemBinding.bind() {
        card.setOnClickListener { onItemClicked(item) }
        ibDelete.setOnClickListener { onDeleteClicked(item) }
        val bmp = BitmapFactory.decodeByteArray(item.codeImage, 0, item.codeImage?.size ?: 0)
        val model = item.data
        if (model is QRCodeModel.PlainModel) {
            val colorInt = ContextCompat.getColor(context, R.color.candy_teal)
            tvLabel.text = "Your code"
            tvShortDesc.text = model.rawValue
            card.setCardBackgroundColor(colorInt)
            ivCode.load(bmp)
            ivImage.load(R.drawable.ic_round_person_24)
        }
    }
}
