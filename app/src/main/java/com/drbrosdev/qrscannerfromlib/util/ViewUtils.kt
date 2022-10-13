package com.drbrosdev.qrscannerfromlib.util

import android.content.res.Resources
import android.widget.TextView
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.view.doOnLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.drbrosdev.qrscannerfromlib.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

fun RecyclerView.heightAsFlow() = callbackFlow {
    kotlinx.coroutines.delay(100)
    doOnLayout {
        trySend(it.measuredHeight)
    }

    awaitClose {  }
}

fun TextView.setDrawable(@DrawableRes res: Int, @DimenRes sizeRes: Int) {
    val drawable = ContextCompat.getDrawable(context, res)
    val size = resources.getDimensionPixelSize(sizeRes)
    drawable?.setBounds(0,0, size, size)
    setCompoundDrawablesRelative(null, null, drawable, null)
}

val Int.dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

fun Fragment.showConfirmDialog(
    title: String = getString(R.string.are_you_sure),
    message: String = "",
    onPositiveClick: () -> Unit
) {
    MaterialAlertDialogBuilder(requireContext(), R.style.ThemeOverlay_App_MaterialAlertDialog)
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton(getString(R.string.yes)) { dialog, _ ->
            onPositiveClick()
            dialog.dismiss()
        }
        .setNegativeButton(getString(R.string.no)) { dialog, _ ->
            dialog.dismiss()
        }
        .create().show()
}