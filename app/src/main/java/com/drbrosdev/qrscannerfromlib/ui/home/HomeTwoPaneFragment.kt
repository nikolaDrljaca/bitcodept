package com.drbrosdev.qrscannerfromlib.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.AbstractListDetailFragment
import androidx.navigation.fragment.NavHostFragment
import com.airbnb.epoxy.EpoxyRecyclerView
import com.drbrosdev.qrscannerfromlib.R
import com.drbrosdev.qrscannerfromlib.databinding.FragmentHomeTwoPaneBinding
import com.drbrosdev.qrscannerfromlib.ui.epoxy.localImageCode
import com.drbrosdev.qrscannerfromlib.ui.epoxy.localImageHeader
import com.drbrosdev.qrscannerfromlib.ui.epoxy.localImageInfo
import com.drbrosdev.qrscannerfromlib.ui.localimage.context
import com.drbrosdev.qrscannerfromlib.util.collectFlow
import com.drbrosdev.qrscannerfromlib.util.decideQrCodeColor
import com.drbrosdev.qrscannerfromlib.util.updateWindowInsets
import com.drbrosdev.qrscannerfromlib.util.viewBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.navigation.koinNavGraphViewModel

class HomeTwoPaneFragment: AbstractListDetailFragment() {
    private val viewModel: HomeViewModel by koinNavGraphViewModel(R.id.nav_graph)
    private val binding: FragmentHomeTwoPaneBinding by viewBinding(FragmentHomeTwoPaneBinding::bind)

    override fun onCreateListPaneView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.list_pane, container, false)
    }

    override fun onCreateDetailPaneNavHostFragment(): NavHostFragment {
        return NavHostFragment.create(R.navigation.two_pane_nav_graph)
    }

    override fun onListPaneViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onListPaneViewCreated(view, savedInstanceState)
        val epoxyView = view as EpoxyRecyclerView

        updateWindowInsets(slidingPaneLayout)

        collectFlow(viewModel.state) { state ->
            epoxyView.withModels {
                localImageHeader {
                    headerText(requireContext().getString(R.string.detected_nqr_codes))
                    id("local_image_header")
                }
                localImageInfo {
                    id("local_image_info")
                    infoText(requireContext().getString(R.string.local_image_scanning_info))
                }

                state.userCodeList.forEach { code ->
                    localImageCode {
                        id(code.id)
                        item(code)
                        colorInt(requireContext().decideQrCodeColor(code))
                        onItemClicked {
                            openDetails(code.id)
                        }
                        onDeleteClicked {
                            slidingPaneLayout.openPane()
                        }
                    }
                }
            }
        }
    }

    private fun openDetails(codeId: Int) {
        val detailNavController = detailPaneNavHostFragment.navController
        detailNavController.navigate(
            R.id.twoPaneCodeDetailFragment,
            bundleOf("code_id" to codeId), //pass args here
            NavOptions.Builder()
                .setPopUpTo(detailNavController.graph.startDestinationRoute, true)
                .apply {
                    if (slidingPaneLayout.isOpen) {
                        setEnterAnim(R.anim.nav_default_enter_anim)
                        setExitAnim(R.anim.nav_default_exit_anim)
                    }
                }
                .build()
        )
        slidingPaneLayout.open()
    }
}