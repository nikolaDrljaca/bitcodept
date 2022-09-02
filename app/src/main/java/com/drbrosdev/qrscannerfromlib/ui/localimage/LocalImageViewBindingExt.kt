package com.drbrosdev.qrscannerfromlib.ui.localimage

import android.content.Context
import androidx.core.content.ContentProviderCompat.requireContext
import com.drbrosdev.qrscannerfromlib.R
import com.drbrosdev.qrscannerfromlib.anims.fadeTo
import com.drbrosdev.qrscannerfromlib.database.QRCodeEntity
import com.drbrosdev.qrscannerfromlib.databinding.FragmentLocalImageBinding
import com.drbrosdev.qrscannerfromlib.ui.epoxy.localImageCode
import com.drbrosdev.qrscannerfromlib.ui.epoxy.localImageHeader
import com.drbrosdev.qrscannerfromlib.ui.epoxy.localImageInfo
import com.drbrosdev.qrscannerfromlib.ui.epoxy.localImageSelectButton
import com.drbrosdev.qrscannerfromlib.util.decideQrCodeColor

val FragmentLocalImageBinding.context: Context
    get() = root.context

fun FragmentLocalImageBinding.bindUiState(
    state: LocalImageViewState,
    onListEmpty: () -> Unit,
    onCodeItemClicked: (Int) -> Unit,
    onCodeDeleteClicked: (QRCodeEntity) -> Unit,
    onSelectImageClicked: () -> Unit
) {
    if (!state.isListEmpty) onListEmpty()
    if (state.errorMessage.isNotBlank()) {
        textViewError.apply {
            text = state.errorMessage
            fadeTo(state.errorMessage.isNotBlank())
            setBackgroundColor(context.getColor(R.color.candy_red))
        }
    }

    rvLocalCodes.withModels {
        localImageHeader {
            headerText(context.getString(R.string.detected_nqr_codes))
            id("local_image_header")
        }
        if (state.isListEmpty) localImageInfo {
            id("local_image_info")
            infoText(context.getString(R.string.local_image_scanning_info))
        }
        state.codes.forEach { qrCodeEntity ->
            qrCodeEntity?.let { code ->
                localImageCode {
                    id(code.id)
                    item(code)
                    colorInt(context.decideQrCodeColor(code))
                    onItemClicked { onCodeItemClicked(it.id) }
                    onDeleteClicked { onCodeDeleteClicked(it) }
                }
            }
        }
        localImageSelectButton {
            id("local_image_select_button")
            onClick { onSelectImageClicked() }
        }
    }
}