package com.drbrosdev.qrscannerfromlib.ui.localimage

import com.drbrosdev.qrscannerfromlib.database.QRCodeEntity

data class LocalImageViewState(
    val codes: List<QRCodeEntity?> = emptyList(),
    val errorMessage: String = ""
) {
    val isListEmpty = codes.isEmpty()
}
