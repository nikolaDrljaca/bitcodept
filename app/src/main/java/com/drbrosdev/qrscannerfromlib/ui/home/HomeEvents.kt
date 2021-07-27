package com.drbrosdev.qrscannerfromlib.ui.home

import com.drbrosdev.qrscannerfromlib.database.QRCodeEntity

sealed class HomeEvents {
    data class ShowCurrentCodeSaved(val id: Int): HomeEvents()
    data class ShowUndoCodeDelete(val code: QRCodeEntity): HomeEvents()
    object ShowSavingCode: HomeEvents()
    object ShowErrorCreatingCodeImage: HomeEvents()
    object ShowFirstUpdateDialog: HomeEvents()
}