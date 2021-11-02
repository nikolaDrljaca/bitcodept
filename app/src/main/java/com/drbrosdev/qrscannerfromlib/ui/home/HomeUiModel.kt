package com.drbrosdev.qrscannerfromlib.ui.home

import com.drbrosdev.qrscannerfromlib.database.QRCodeEntity

data class HomeUiModel(
    val isLoading: Boolean = true,
    val codeList: List<QRCodeEntity> = emptyList(),
    val userCodeList: List<QRCodeEntity> = emptyList()
) {
    val isEmpty = codeList.isEmpty() and userCodeList.isEmpty()
}