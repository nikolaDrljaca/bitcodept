package com.drbrosdev.qrscannerfromlib.ui.epoxy

import android.view.ViewGroup
import android.widget.FrameLayout
import coil.load
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.drbrosdev.qrscannerfromlib.R
import com.drbrosdev.qrscannerfromlib.anims.fadeTo
import com.drbrosdev.qrscannerfromlib.database.QRCodeEntity
import com.drbrosdev.qrscannerfromlib.databinding.ModelActionButtonsBinding
import com.drbrosdev.qrscannerfromlib.databinding.ModelBitcodeptHeaderBinding
import com.drbrosdev.qrscannerfromlib.databinding.ModelCodeHeaderBinding
import com.drbrosdev.qrscannerfromlib.databinding.ModelCreateCodeBinding
import com.drbrosdev.qrscannerfromlib.model.QRCodeModel
import com.drbrosdev.qrscannerfromlib.util.QRGenUtils

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
abstract class CreateCodeItemEpoxyModel :
    ViewBindingKotlinModel<ModelCreateCodeBinding>(R.layout.model_create_code) {
    @EpoxyAttribute
    lateinit var onItemClicked: () -> Unit

    override fun ModelCreateCodeBinding.bind() {
        card.setOnClickListener { onItemClicked() }

        tvLabel.text = "Create code"
        tvShortDesc.text = "Create Your own QR code!"
    }
}

@EpoxyModelClass
abstract class ActionButtonsEpoxyModel :
    ViewBindingKotlinModel<ModelActionButtonsBinding>(R.layout.model_action_buttons) {

    @EpoxyAttribute
    lateinit var onLocalScanClicked: () -> Unit

    @EpoxyAttribute
    lateinit var onCameraScanClicked: () -> Unit

    override fun ModelActionButtonsBinding.bind() {
        buttonNewScan.setOnClickListener { onCameraScanClicked() }
        buttonLocalImageScan.setOnClickListener { onLocalScanClicked() }
    }
}

@EpoxyModelClass
abstract class BitcodeptHeaderEpoxyModel :
    ViewBindingKotlinModel<ModelBitcodeptHeaderBinding>(R.layout.model_bitcodept_header) {

    @EpoxyAttribute
    lateinit var onInfoClicked: () -> Unit

    @EpoxyAttribute
    lateinit var onDeleteClicked: () -> Unit

    @EpoxyAttribute
    var deleteButtonVisibility = false

    @EpoxyAttribute
    var headerTextVisibility = false

    override fun ModelBitcodeptHeaderBinding.bind() {
        imageButtonDeleteAll.apply {
            setOnClickListener { onDeleteClicked() }
            fadeTo(deleteButtonVisibility)
        }
        imageButtonInfo.setOnClickListener { onInfoClicked() }
        imageViewAppIcon.apply {
            val shape = shapeAppearanceModel.toBuilder()
                .setAllCornerSizes(20f)
                .build()
            shapeAppearanceModel = shape
        }
        constraintLayoutHeaderText.fadeTo(headerTextVisibility)
    }
}