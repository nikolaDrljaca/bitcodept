package com.drbrosdev.qrscannerfromlib.ui.home

import android.graphics.Canvas
import android.view.View
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getColor
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.navigation.fragment.findNavController
import com.airbnb.epoxy.EpoxyTouchHelper
import com.drbrosdev.qrscannerfromlib.R
import com.drbrosdev.qrscannerfromlib.anims.fadeTo
import com.drbrosdev.qrscannerfromlib.database.QRCodeEntity
import com.drbrosdev.qrscannerfromlib.databinding.FragmentHomeTwoPaneBinding
import com.drbrosdev.qrscannerfromlib.ui.epoxy.LocalImageCodeModel
import com.drbrosdev.qrscannerfromlib.ui.epoxy.actionButtons
import com.drbrosdev.qrscannerfromlib.ui.epoxy.bitcodeptHeader
import com.drbrosdev.qrscannerfromlib.ui.epoxy.createCodeItem
import com.drbrosdev.qrscannerfromlib.ui.epoxy.homeModelListHeader
import com.drbrosdev.qrscannerfromlib.ui.epoxy.localImageCode
import com.drbrosdev.qrscannerfromlib.ui.epoxy.spacer
import com.drbrosdev.qrscannerfromlib.util.WindowSizeClass
import com.drbrosdev.qrscannerfromlib.util.decideQrCodeColor
import com.drbrosdev.qrscannerfromlib.util.dp

fun FragmentHomeTwoPaneBinding.setupUiForCompact() {
    constraintLayoutRail.isVisible = false
    slidingPaneLayout.updatePadding(left = 0.dp)
}

fun FragmentHomeTwoPaneBinding.setupUiForExpanded() {
    constraintLayoutRail.isVisible = true
    slidingPaneLayout.updatePadding(left = 72.dp)
}

fun FragmentHomeTwoPaneBinding.bindUiState(
    state: HomeUiModel,
    headerTextVisibility: Boolean,
    onCreateCodeClicked: () -> Unit,
    onCodeItemClicked: (QRCodeEntity) -> Unit,
    onCodeDelete: (QRCodeEntity) -> Unit,
    onLocalScanClicked: () -> Unit,
    onCameraScanClicked: () -> Unit,
    onInfoClicked: () -> Unit,
    onDeleteAll: () -> Unit,
) {
    epoxyViewListPane.withModels {
        bitcodeptHeader {
            id("bitcodept_header")
            onInfoClicked { onInfoClicked() }
            onDeleteClicked { onDeleteAll() }
            deleteButtonVisibility(!state.isEmpty)
            headerTextVisibility(headerTextVisibility)
        }

        if(headerTextVisibility) {
            actionButtons {
                id("action_buttons")
                onLocalScanClicked { onLocalScanClicked() }
                onCameraScanClicked { onCameraScanClicked() }
            }
        }

        createCodeItem {
            id("create_code_item")
            onItemClicked { onCreateCodeClicked() }
        }

        if(!state.isCreatedCodesListEmpty)
            homeModelListHeader {
                text(root.context.getString(R.string.created_header_vertical))
                id("created_codes_header")
            }

        state.userCodeList.forEach { code ->
            localImageCode {
                id(code.id)
                item(code)
                showDeleteButton(false)
                colorInt(root.context.decideQrCodeColor(code))
                onItemClicked { onCodeItemClicked(it) }
                onDeleteClicked {  }
            }
        }

        if (!state.isScannedCodeListEmpty)
            homeModelListHeader {
                text(root.context.getString(R.string.scanned_header_vertical))
                id("user_codes_header")
            }

        state.codeList.forEach { code ->
            localImageCode {
                id(code.id)
                item(code)
                showDeleteButton(false)
                colorInt(root.context.decideQrCodeColor(code))
                onItemClicked { onCodeItemClicked(it) }
                onDeleteClicked {  }
            }
        }

        spacer {
            id("local_spacer")
            height(80.dp)
        }
    }

    imageButtonDeleteAll.fadeTo(state.isEmpty)

    val delete = ContextCompat.getDrawable(
        root.context,
        R.drawable.ic_round_delete_outline_24
    )

    EpoxyTouchHelper.initSwiping(epoxyViewListPane)
        .left()
        .withTarget(LocalImageCodeModel::class.java)
        .andCallbacks(object : EpoxyTouchHelper.SwipeCallbacks<LocalImageCodeModel>() {
            override fun onSwipeCompleted(
                model: LocalImageCodeModel?,
                itemView: View?,
                position: Int,
                direction: Int
            ) {
                model?.let { onCodeDelete(model.item) }
            }

            override fun onSwipeProgressChanged(
                model: LocalImageCodeModel?,
                itemView: View?,
                swipeProgress: Float,
                canvas: Canvas?
            ) {
                itemView?.let { view ->
                    view.alpha = swipeProgress + 1
                    val itemHeight = view.bottom - view.top
                    delete?.setTint(root.context.getColor(R.color.candy_red))

                    val iconTop = view.top + (itemHeight - delete!!.intrinsicHeight) / 2
                    val iconMargin = (itemHeight - delete.intrinsicHeight) / 2
                    val iconLeft = view.right - iconMargin - delete.intrinsicWidth
                    val iconRight = view.right - iconMargin
                    val iconBottom = iconTop + delete.intrinsicHeight

                    delete.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                    delete.draw(canvas!!)
                }
            }

        })
}