package com.drbrosdev.qrscannerfromlib.ui.codeshare

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.drbrosdev.qrscannerfromlib.R
import com.drbrosdev.qrscannerfromlib.databinding.FragmentCodeShareBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CodeShareFragment: BottomSheetDialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.ThemeOverlay_Demo_BottomSheetDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_code_share, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentCodeShareBinding.bind(view)

        binding.imageViewAppIcon.apply {
            val shape = shapeAppearanceModel.toBuilder()
                .setAllCornerSizes(20f)
                .build()
            shapeAppearanceModel = shape
        }
    }
}