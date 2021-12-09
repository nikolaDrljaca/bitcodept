package com.drbrosdev.qrscannerfromlib.ui.epoxy

import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.drbrosdev.qrscannerfromlib.R
import com.drbrosdev.qrscannerfromlib.databinding.ModelLicenseItemBinding
import com.drbrosdev.qrscannerfromlib.ui.licenses.LicensesViewModel

@EpoxyModelClass
abstract class LicenseItemEpoxyModel :
    ViewBindingKotlinModel<ModelLicenseItemBinding>(R.layout.model_license_item) {

    @EpoxyAttribute
    lateinit var item: LicensesViewModel.LicenseModel

    @EpoxyAttribute
    lateinit var onClick: () -> Unit

    override fun ModelLicenseItemBinding.bind() {
        textViewLink.text = item.link
        textViewTitle.text = item.title
        textViewLink.setOnClickListener { onClick() }
    }
}