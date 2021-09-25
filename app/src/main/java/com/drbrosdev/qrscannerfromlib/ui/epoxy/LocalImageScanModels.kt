package com.drbrosdev.qrscannerfromlib.ui.epoxy

import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.drbrosdev.qrscannerfromlib.R
import com.drbrosdev.qrscannerfromlib.databinding.DetectedCodesHeaderBinding
import com.drbrosdev.qrscannerfromlib.databinding.DetectedCodesInfoBinding

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

    @EpoxyAttribute
    lateinit var onSelectImageClicked: () -> Unit

    override fun DetectedCodesInfoBinding.bind() {
        tvInfoText.text = infoText
        buttonSelectImage.setOnClickListener {
            onSelectImageClicked()
        }
    }
}