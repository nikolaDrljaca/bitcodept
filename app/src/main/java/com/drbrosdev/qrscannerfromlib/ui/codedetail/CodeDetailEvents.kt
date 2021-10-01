package com.drbrosdev.qrscannerfromlib.ui.codedetail

sealed class CodeDetailEvents {
    data class ShowThrownErrorMessage(val msg: String): CodeDetailEvents()
}
