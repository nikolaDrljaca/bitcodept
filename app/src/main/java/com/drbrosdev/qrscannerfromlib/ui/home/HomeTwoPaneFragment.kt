package com.drbrosdev.qrscannerfromlib.ui.home

import android.content.res.Configuration
import android.os.Bundle
import android.os.Parcelable
import android.text.TextUtils.replace
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updateMargins
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.AbstractListDetailFragment
import androidx.navigation.fragment.NavHostFragment
import androidx.slidingpanelayout.widget.SlidingPaneLayout
import com.airbnb.epoxy.EpoxyRecyclerView
import com.drbrosdev.qrscannerfromlib.R
import com.drbrosdev.qrscannerfromlib.anims.fadeTo
import com.drbrosdev.qrscannerfromlib.databinding.FragmentHomeTwoPaneBinding
import com.drbrosdev.qrscannerfromlib.ui.codedetail.CodeDetailFragment
import com.drbrosdev.qrscannerfromlib.ui.epoxy.actionButtons
import com.drbrosdev.qrscannerfromlib.ui.epoxy.bitcodeptHeader
import com.drbrosdev.qrscannerfromlib.ui.epoxy.localImageCode
import com.drbrosdev.qrscannerfromlib.ui.epoxy.localImageHeader
import com.drbrosdev.qrscannerfromlib.ui.epoxy.localImageInfo
import com.drbrosdev.qrscannerfromlib.ui.epoxy.spacer
import com.drbrosdev.qrscannerfromlib.ui.localimage.context
import com.drbrosdev.qrscannerfromlib.util.WindowSizeClass
import com.drbrosdev.qrscannerfromlib.util.collectFlow
import com.drbrosdev.qrscannerfromlib.util.decideQrCodeColor
import com.drbrosdev.qrscannerfromlib.util.dp
import com.drbrosdev.qrscannerfromlib.util.updateWindowInsets
import com.drbrosdev.qrscannerfromlib.util.viewBinding
import com.google.android.material.imageview.ShapeableImageView
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.androidx.fragment.android.replace
import org.koin.androidx.navigation.koinNavGraphViewModel

class HomeTwoPaneFragment : Fragment(R.layout.fragment_home_two_pane) {
    private val viewModel: HomeViewModel by koinNavGraphViewModel(R.id.nav_graph)
    private val binding: FragmentHomeTwoPaneBinding by viewBinding(FragmentHomeTwoPaneBinding::bind)

    private val windowSizeClass = MutableStateFlow(WindowSizeClass.COMPACT)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateWindowInsets(binding.root)
        windowSizeClass.update { WindowSizeClass.computeWindowSizeClass(requireActivity()) }

        binding.frameLayoutRoot.addView(object : View(requireContext()) {
            override fun onConfigurationChanged(newConfig: Configuration?) {
                super.onConfigurationChanged(newConfig)
                windowSizeClass.update { WindowSizeClass.computeWindowSizeClass(requireActivity()) }
            }
        })

        when (windowSizeClass.value) {
            WindowSizeClass.COMPACT -> binding.setupUiForCompact()
            WindowSizeClass.MEDIUM -> binding.setupUiForExpanded()
            WindowSizeClass.EXPANDED -> binding.setupUiForExpanded()
        }

        collectFlow(viewModel.state) { state ->
            binding.epoxyViewListPane.withModels {
                bitcodeptHeader {
                    id("bitcodept_header")
                    onInfoClicked {  }
                    onDeleteClicked {  }
                    deleteButtonVisibility(state.isEmpty)
                    headerTextVisibility(windowSizeClass.value == WindowSizeClass.COMPACT)
                }

                actionButtons {
                    id("action_buttons")
                    onLocalScanClicked {  }
                    onCameraScanClicked {  }
                }

                state.userCodeList.forEach { code ->
                    localImageCode {
                        id(code.id)
                        item(code)
                        showDeleteButton(false)
                        colorInt(requireContext().decideQrCodeColor(code))
                        onItemClicked {
                            openDetails(code.id)
                        }
                        onDeleteClicked { }
                    }
                }

                spacer {
                    id("local_spacer")
                    height(80.dp)
                }
            }
        }

        binding.imageViewAppIcon.apply {
            val shape = shapeAppearanceModel.toBuilder()
                .setAllCornerSizes(20f)
                .build()
            shapeAppearanceModel = shape
        }
    }

    private fun openDetails(codeId: Int) {
        childFragmentManager.commit {
            setReorderingAllowed(true)
            replace<CodeDetailFragment>(
                R.id.detail_container,
                bundleOf("code_id" to codeId)
            )
            // If we're already open and the detail pane is visible,
            // crossfade between the fragments.
            if (binding.slidingPaneLayout.isOpen) {
                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            }
        }
        binding.slidingPaneLayout.open()
    }
}