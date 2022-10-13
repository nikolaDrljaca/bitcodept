package com.drbrosdev.qrscannerfromlib.ui.codedetail

import com.drbrosdev.qrscannerfromlib.database.QRCodeEntity

data class CodeDetailUiModel(
    val code: QRCodeEntity? = null,
    val isLoading: Boolean = false
)