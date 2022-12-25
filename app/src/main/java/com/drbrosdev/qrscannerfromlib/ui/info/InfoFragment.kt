package com.drbrosdev.qrscannerfromlib.ui.info

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.drbrosdev.qrscannerfromlib.R
import com.drbrosdev.qrscannerfromlib.databinding.FragmentInfoBinding
import com.drbrosdev.qrscannerfromlib.ui.epoxy.infoDevHeader
import com.drbrosdev.qrscannerfromlib.ui.epoxy.infoItem
import com.drbrosdev.qrscannerfromlib.util.updateWindowInsets
import com.drbrosdev.qrscannerfromlib.util.viewBinding
import com.google.android.material.transition.MaterialSharedAxis

class InfoFragment: Fragment(R.layout.fragment_info) {
    private val binding by viewBinding(FragmentInfoBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateWindowInsets(binding.root)

        binding.apply {
            recyclerView.setItemSpacingPx(12)
            recyclerView.withModels {
                infoDevHeader {
                    spanSizeOverride { totalSpanCount, _, _ ->
                        totalSpanCount
                    }
                    id("dev_header")
                    versionText("BitCodept 5.1")
                    onNikolaGhClick {  }
                    onNikolaLnClick {  }
                    onOgnjenGhClick {  }
                    onOgnjenLnClick {  }
                }

                InfoItem.items.forEach { infoItem ->
                    infoItem {
                        id(infoItem.id)
                        infoItem(infoItem)
                        onClick {

                        }
                    }
                }
            }
        }
    }

}