package com.drbrosdev.qrscannerfromlib.ui.codedetail

import com.drbrosdev.qrscannerfromlib.database.QRCodeEntity

data class CodeDetailUiModel(
    val code: QRCodeEntity,
    val isLoading: Boolean
)