package com.drbrosdev.qrscannerfromlib.ui.epoxy

import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.drbrosdev.qrscannerfromlib.R
import com.drbrosdev.qrscannerfromlib.databinding.ModelSupportTextBinding
import com.drbrosdev.qrscannerfromlib.databinding.ModelSupportTierBinding


@EpoxyModelClass
abstract class SupportTierItemEpoxyModel :
    ViewBindingKotlinModel<ModelSupportTierBinding>(R.layout.model_support_tier) {

    @EpoxyAttribute
    lateinit var onItemClicked: () -> Unit

    @EpoxyAttribute
    var tierText: String = ""

    @EpoxyAttribute
    var price: String = ""

    @EpoxyAttribute
    var colorInt: Int = 0

    override fun ModelSupportTierBinding.bind() {
        card.setOnClickListener { onItemClicked() }

        tvPrice.text = price
        tvTier.text = tierText

        card.setCardBackgroundColor(colorInt)
    }
}

@EpoxyModelClass
abstract class SupportTextEpoxyModel :
    ViewBindingKotlinModel<ModelSupportTextBinding>(R.layout.model_support_text) {

    override fun ModelSupportTextBinding.bind() {
        //TODO("Not yet implemented")
    }
}