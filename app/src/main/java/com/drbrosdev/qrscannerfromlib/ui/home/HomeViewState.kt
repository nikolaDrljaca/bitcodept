package com.drbrosdev.qrscannerfromlib.ui.home

import com.drbrosdev.qrscannerfromlib.database.QRCodeEntity
import com.drbrosdev.qrscannerfromlib.util.AsyncModel

data class HomeViewState(
    private val _codeList: AsyncModel<List<QRCodeEntity>> = AsyncModel.Loading
) {
    val isLoading = _codeList is AsyncModel.Loading
    val isError = _codeList is AsyncModel.Fail
    val errorMessage: String? =
        if (isError) (_codeList as AsyncModel.Fail).t.localizedMessage else "Something went wrong."

    val isEmpty = if (_codeList is AsyncModel.Success) _codeList.data.isEmpty() else false
    val isSuccess = _codeList is AsyncModel.Success

    val codeList = if (_codeList is AsyncModel.Success) _codeList.data else emptyList()
}