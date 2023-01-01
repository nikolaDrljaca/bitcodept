package com.drbrosdev.qrscannerfromlib.ui.info

sealed class InfoItem(open val id: String) {
    data class Bug(override val id: String = "bug") : InfoItem(id)
    data class Share(override val id: String = "share") : InfoItem(id)
    data class Star(override val id: String = "star") : InfoItem(id)
    data class Support(override val id: String = "support") : InfoItem(id)
    data class OpenSourceCode(override val id: String = "open_sc") : InfoItem(id)
    data class PrivacyPolicy(override val id: String = "priv_policy"): InfoItem(id)

    companion object {
        val items = listOf(
            InfoItem.PrivacyPolicy(),
            InfoItem.Bug(),
            InfoItem.Share(),
            InfoItem.Star(),
            InfoItem.Support(),
            InfoItem.OpenSourceCode(),
        )
    }
}
