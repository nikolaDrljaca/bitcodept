package com.drbrosdev.qrscannerfromlib.ui.support

import com.android.billingclient.api.Purchase

sealed interface SupportEvents {
    object SendErrorToast: SupportEvents
    data class SendPurchases(val purchases: List<Purchase>): SupportEvents
}