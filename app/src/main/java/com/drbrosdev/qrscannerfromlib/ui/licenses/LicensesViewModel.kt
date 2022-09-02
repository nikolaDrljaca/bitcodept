package com.drbrosdev.qrscannerfromlib.ui.licenses

import androidx.lifecycle.ViewModel

class LicensesViewModel: ViewModel() {

    data class LicenseModel(
        val title: String,
        val link: String
    )

    val licenses = listOf(
        LicenseModel("Quickie","https://github.com/G00fY2/quickie"),
        LicenseModel("Coil Image Loading","https://coil-kt.github.io/coil/getting_started/"),
        LicenseModel("Lottie","http://airbnb.io/lottie/#/android"),
        LicenseModel("Lottie Animation by Manoj Kumar","https://lottiefiles.com/manoj61"),
        LicenseModel("Lottie Animation by Yannick Gbaguidi","https://lottiefiles.com/77597-coffee"),
        LicenseModel("Lottie Animation by Daniela Stella","https://lottiefiles.com/user/313969"),
        LicenseModel("Lottie Animation by Emas Didik Prasetyo","https://lottiefiles.com/74797-thank-you-with-confetti"),
        LicenseModel("Design","https://dribbble.com/shots/14114678-Keychain-manager-app-concept"),
        LicenseModel("Epoxy","https://github.com/airbnb/epoxy"),
    )
}