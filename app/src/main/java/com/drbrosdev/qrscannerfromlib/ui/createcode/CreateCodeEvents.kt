package com.drbrosdev.qrscannerfromlib.ui.createcode

sealed class CreateCodeEvents {
    object ShowLoading: CreateCodeEvents()
    object ShowCodeSaved: CreateCodeEvents()
    object ShowError: CreateCodeEvents()
    object CodeTextIsEmpty: CreateCodeEvents()
}