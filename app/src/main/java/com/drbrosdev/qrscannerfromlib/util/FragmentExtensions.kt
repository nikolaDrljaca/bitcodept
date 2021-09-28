package com.drbrosdev.qrscannerfromlib.util

import android.annotation.SuppressLint
import android.app.Activity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import com.drbrosdev.qrscannerfromlib.R
import com.drbrosdev.qrscannerfromlib.database.QRCodeEntity
import com.drbrosdev.qrscannerfromlib.model.QRCodeModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

/*
Fragment extensions for commonly used things

Keep in mind, toasts are getting overhauled in Android 12 to look much more
appealing, so their usage could be more encouraged for informative purposes where actions aren't
needed.
 */
fun Fragment.showShortToast(message: String) =
    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

fun Fragment.showLongToast(message: String) =
    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()

fun Fragment.getColor(@ColorRes res: Int) = ContextCompat.getColor(requireContext(), res)

fun Fragment.showSnackbarShort(message: String, anchor: View? = null) =
    Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT)
        .setAnchorView(anchor)
        .show()

fun Fragment.showSnackbarLong(message: String, anchor: View? = null) =
    Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG)
        .setAnchorView(anchor)
        .show()

fun Fragment.showSnackbarShortWithAction(
    message: String,
    actionText: String,
    anchor: View? = null,
    onAction: () -> Unit
) = Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT)
    .setAnchorView(anchor)
    .setAction(actionText) { onAction() }
    .show()

fun Fragment.showSnackbarLongWithAction(
    message: String,
    actionText: String,
    anchor: View? = null,
    onAction: () -> Unit
) = Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG)
    .setAnchorView(anchor)
    .setAction(actionText) { onAction() }
    .show()

/*
Apply window insets to current fragment, allows fullscreen apps to listen to heights of status/nav bars.
To use with properly set up theme xml files.

Read article for more information:
    https://medium.com/androiddevelopers/gesture-navigation-going-edge-to-edge-812f62e4e83e

Not necessarily tied to the fragment but scoping it as such prevents this from being called in
random places that are not activities/fragments.
 */
fun Fragment.updateWindowInsets(rootView: View) {
    ViewCompat.setOnApplyWindowInsetsListener(rootView) { v, insets ->
        v.updatePadding(
            bottom = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom,
            top = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top
        )
        insets
    }
}

fun Activity.updateWindowInsets(rootView: View) {
    ViewCompat.setOnApplyWindowInsetsListener(rootView) { v, insets ->
        v.updatePadding(
            bottom = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom,
            top = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top
        )
        insets
    }
}

/*
Creates a fullscreen dialog that displays an infinite loading animations(lottie view, very easy).
For Lottie:
    Download free anim from lottiefiles.com and use LottieAnimationView in layout file
    The file should be a json and you store it in res/raw
The only way to get the rounded corners and style the dialog is to create a custom style like
its done in this project.
 */

@SuppressLint("InflateParams")
fun Fragment.createLoadingDialog(): AlertDialog {
    val inflater = requireActivity().layoutInflater
    return MaterialAlertDialogBuilder(requireContext(), R.style.ThemeOverlay_App_MaterialAlertDialog)
        .setView(inflater.inflate(R.layout.fragment_loading, null))
        .create()
}

fun Fragment.hideKeyboard() {
    val imm: InputMethodManager =
        requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(requireView().windowToken, 0)
}

fun Fragment.decideQrCodeColor(code: QRCodeEntity): Int {
    return when(code.data) {
        is QRCodeModel.ContactInfoModel -> getColor(R.color.candy_yellow)
        is QRCodeModel.EmailModel -> getColor(R.color.candy_blue)
        is QRCodeModel.GeoPointModel -> getColor(R.color.candy_purple)
        is QRCodeModel.PhoneModel -> getColor(R.color.candy_green)
        is QRCodeModel.PlainModel -> getColor(R.color.candy_teal)
        is QRCodeModel.SmsModel -> getColor(R.color.candy_orange)
        is QRCodeModel.UrlModel -> getColor(R.color.candy_red)
    }
}

fun Fragment.getCodeColorListAsMap(): Map<String, Int> {
    return mapOf(
        "sms" to getColor(R.color.candy_orange),
        "contact" to getColor(R.color.candy_yellow),
        "email" to getColor(R.color.candy_blue),
        "geo" to getColor(R.color.candy_purple),
        "phone" to getColor(R.color.candy_green),
        "text" to getColor(R.color.candy_teal),
        "url" to getColor(R.color.candy_red),
    )
}