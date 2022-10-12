package com.drbrosdev.qrscannerfromlib.ui.home

import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import com.drbrosdev.qrscannerfromlib.databinding.FragmentHomeTwoPaneBinding
import com.drbrosdev.qrscannerfromlib.util.dp

fun FragmentHomeTwoPaneBinding.setupUiForCompact() {
    constraintLayoutRail.isVisible = false
    slidingPaneLayout.updatePadding(left = 0.dp)
}

fun FragmentHomeTwoPaneBinding.setupUiForExpanded() {
    constraintLayoutRail.isVisible = true
    slidingPaneLayout.updatePadding(left = 72.dp)
}