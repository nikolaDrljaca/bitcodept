package com.drbrosdev.qrscannerfromlib.ui.support

import com.android.billingclient.api.SkuDetails

data class SupportItem(
    val skuDetails: SkuDetails,
    val color: Int
) {
    val title = filter(skuDetails.title)
    val price = skuDetails.price
    val description = skuDetails.description

    private fun filter(input: String): String {
        val split = input.split(" ")
        return "${split[0]} ${split[1]}"
    }
}