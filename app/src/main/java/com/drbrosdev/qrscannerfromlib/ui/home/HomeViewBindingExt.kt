package com.drbrosdev.qrscannerfromlib.ui.home

import android.content.Context
import androidx.navigation.fragment.findNavController
import com.drbrosdev.qrscannerfromlib.R
import com.drbrosdev.qrscannerfromlib.anims.fadeTo
import com.drbrosdev.qrscannerfromlib.database.QRCodeEntity
import com.drbrosdev.qrscannerfromlib.databinding.FragmentHomeBinding
import com.drbrosdev.qrscannerfromlib.ui.epoxy.createCodeItem
import com.drbrosdev.qrscannerfromlib.ui.epoxy.homeModelListHeader
import com.drbrosdev.qrscannerfromlib.ui.epoxy.qRCodeListItem
import com.drbrosdev.qrscannerfromlib.ui.epoxy.spacer
import com.drbrosdev.qrscannerfromlib.util.decideQrCodeColor
import com.google.android.material.transition.MaterialSharedAxis

val FragmentHomeBinding.context: Context
    get() = root.context

fun FragmentHomeBinding.bindUiState(
    state: HomeUiModel,
    onCreateCodeClicked: () -> Unit,
    onCodeItemClicked: (QRCodeEntity) -> Unit,
    onCodeDeleteClicked: (QRCodeEntity) -> Unit
) {
    progressBar.fadeTo(state.isLoading)
    imageButtonDeleteAll.fadeTo(state.isEmpty)

    recyclerViewCodes.withModels {
        spacer { id("first_spacer") }
        createCodeItem {
            id("create_code_card")
            height(state.codeItemHeight)
            colorInt(context.getColor(R.color.background))
            imageColor(context.getColor(R.color.card_border))
            onItemClicked { onCreateCodeClicked() }
        }

        if(!state.isCreatedCodesListEmpty)
            homeModelListHeader {
                text(context.getString(R.string.created_header_vertical))
                id("created_codes_header")
            }

        state.userCodeList.forEach { code ->
            qRCodeListItem {
                id(code.id)
                onItemClicked { onCodeItemClicked(it) }
                onDeleteClicked { onCodeDeleteClicked(it) }
                item(code)
                colorInt(context.decideQrCodeColor(code))
                height(state.codeItemHeight)
            }
        }

        if (!state.isScannedCodeListEmpty)
            homeModelListHeader {
                text(context.getString(R.string.scanned_header_vertical))
                id("user_codes_header")
            }

        state.codeList.forEach { code ->
            qRCodeListItem {
                id(code.id)
                onItemClicked { onCodeItemClicked(it) }
                onDeleteClicked { onCodeDeleteClicked(it) }
                item(code)
                colorInt(context.decideQrCodeColor(code))
                height(state.codeItemHeight)
            }
        }
        spacer { id("second_spacer") }

    }
}