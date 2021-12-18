package com.drbrosdev.qrscannerfromlib.ui.support

sealed interface SupportEvents {
    object SendErrorToast: SupportEvents
}