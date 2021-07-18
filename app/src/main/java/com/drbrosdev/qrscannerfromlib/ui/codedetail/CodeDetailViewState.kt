package com.drbrosdev.qrscannerfromlib.ui.codedetail

import com.drbrosdev.qrscannerfromlib.database.QRCodeEntity
import com.drbrosdev.qrscannerfromlib.util.AsyncModel

data class CodeDetailViewState(
    private val _code: AsyncModel<QRCodeEntity> = AsyncModel.Loading
) {
    val isLoading = _code is AsyncModel.Loading
    val isError = _code is AsyncModel.Fail
    val errorMessage: String? =
        if (isError) (_code as AsyncModel.Fail).t.localizedMessage else "Something went wrong."

    val code = if (_code is AsyncModel.Success) _code.data else null
}