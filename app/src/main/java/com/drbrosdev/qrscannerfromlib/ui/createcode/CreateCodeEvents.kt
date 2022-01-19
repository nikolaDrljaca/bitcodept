package com.drbrosdev.qrscannerfromlib.ui.createcode

sealed interface CreateCodeEvents {
    object ShowLoading : CreateCodeEvents
    object ShowCodeSaved : CreateCodeEvents
    object CodeTextIsEmpty : CreateCodeEvents
    object CompleteAndNavigateUp: CreateCodeEvents
}