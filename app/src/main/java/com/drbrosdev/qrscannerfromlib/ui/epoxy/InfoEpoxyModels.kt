package com.drbrosdev.qrscannerfromlib.ui.epoxy

import coil.load
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.drbrosdev.qrscannerfromlib.R
import com.drbrosdev.qrscannerfromlib.databinding.ModelInfoDevsBinding
import com.drbrosdev.qrscannerfromlib.databinding.ModelInfoItemBinding
import com.drbrosdev.qrscannerfromlib.ui.epoxy.ViewBindingKotlinModel
import com.drbrosdev.qrscannerfromlib.ui.info.InfoItem

@EpoxyModelClass
abstract class InfoItemEpoxyModel
    : ViewBindingKotlinModel<ModelInfoItemBinding>(R.layout.model_info_item) {

    @EpoxyAttribute
    lateinit var infoItem: InfoItem

    @EpoxyAttribute
    lateinit var onClick: () -> Unit

    override fun ModelInfoItemBinding.bind() {
        card.setOnClickListener { onClick() }

        when (infoItem) {
            is InfoItem.Bug -> {
                textViewTitle.text = "Report a Bug"
                textViewDesc.text = "Tell us what went wrong."
                imageViewIcon.load(R.drawable.bug_solid)
            }
            is InfoItem.OpenSourceCode -> {
                textViewTitle.text = "Open Source Libraries"
                textViewDesc.text = "They helped make it happen."
                imageViewIcon.load(R.drawable.code_solid)
            }
            is InfoItem.Share -> {
                textViewTitle.text = "Share"
                textViewDesc.text = "Spread the love."
                imageViewIcon.load(R.drawable.share_nodes_solid)
            }
            is InfoItem.Star -> {
                textViewTitle.text = "Rate this App"
                textViewDesc.text = "Leave a review."
                imageViewIcon.load(R.drawable.star_solid)
            }
            is InfoItem.Support -> {
                textViewTitle.text = "Support Development"
                textViewDesc.text = "A little suport can go a long way."
                imageViewIcon.load(R.drawable.money_bills_solid)
            }
            is InfoItem.PrivacyPolicy -> {
                textViewTitle.text = "Privacy Policy"
                textViewDesc.text = "What happens to your data?"
                imageViewIcon.load(R.drawable.shield_solid)
            }
        }
    }
}

@EpoxyModelClass
abstract class InfoDevHeaderEpoxyModel
    : ViewBindingKotlinModel<ModelInfoDevsBinding>(R.layout.model_info_devs) {

    @EpoxyAttribute
    lateinit var versionText: String

    @EpoxyAttribute
    lateinit var onNikolaGhClick: () -> Unit

    @EpoxyAttribute
    lateinit var onNikolaLnClick: () -> Unit

    @EpoxyAttribute
    lateinit var onOgnjenGhClick: () -> Unit

    @EpoxyAttribute
    lateinit var onOgnjenLnClick: () -> Unit

    override fun ModelInfoDevsBinding.bind() {
        textViewVersion.text = versionText

        imageViewGhNikola.setOnClickListener { onNikolaGhClick() }
        imageViewLnNikola.setOnClickListener { onNikolaLnClick() }
        imageViewGhOgnjen.setOnClickListener { onOgnjenGhClick() }
        imageViewLnOgnjen.setOnClickListener { onOgnjenLnClick() }
    }
}