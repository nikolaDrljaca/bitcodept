package com.drbrosdev.qrscannerfromlib.ui.localimage

sealed class LocalImageEvents {
    object ShowLoading: LocalImageEvents()
    object ShowEmptyResult: LocalImageEvents()
}
