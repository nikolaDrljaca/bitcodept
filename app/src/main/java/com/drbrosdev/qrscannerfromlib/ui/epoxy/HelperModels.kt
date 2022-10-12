package com.drbrosdev.qrscannerfromlib.ui.epoxy

import androidx.core.view.updateLayoutParams
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.drbrosdev.qrscannerfromlib.R
import com.drbrosdev.qrscannerfromlib.databinding.ModelSpacerBinding

@EpoxyModelClass
abstract class SpacerEpoxyModel :
    ViewBindingKotlinModel<ModelSpacerBinding>(R.layout.model_spacer) {

    @EpoxyAttribute
    var height: Int = 0

    override fun ModelSpacerBinding.bind() {
        spacer.updateLayoutParams {
            this.height = this@SpacerEpoxyModel.height
            width = 0
        }
    }

}