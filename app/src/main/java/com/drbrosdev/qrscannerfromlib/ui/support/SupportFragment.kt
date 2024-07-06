package com.drbrosdev.qrscannerfromlib.ui.support

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.drbrosdev.qrscannerfromlib.R
import com.drbrosdev.qrscannerfromlib.databinding.FragmentSupportBinding
import com.drbrosdev.qrscannerfromlib.util.updateWindowInsets
import com.google.android.material.transition.MaterialSharedAxis

class SupportFragment : Fragment(R.layout.fragment_support) {
//    private val billingWrapper by inject<BillingClientWrapper>()
//    private val viewModel: SupportViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().window.statusBarColor = requireContext().getColor(R.color.background)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)

        val binding = FragmentSupportBinding.bind(view)
        updateWindowInsets(binding.root)

//        billingWrapper.queryProducts()

        /*
        collectFlow(viewModel.events) {
            when(it) {
                SupportEvents.SendErrorToast -> {
                    showShortToast("Something went wrong.")
                }
            }
        }

        collectFlow(billingWrapper.productsFlow) {
            when(it) {
                is QueriedProducts.Success -> {
                    viewModel.setSkuDetails(it.products, requireContext().getCodeColorListAsMap())
                }
                is QueriedProducts.Failure -> {
                    viewModel.setFailure()
                }
            }
        }

        collectFlow(billingWrapper.purchaseFlow) {
            when (it) {
                is PurchaseResult.Failure -> {
                    //error occurred or user cancelled
                }
                is PurchaseResult.Success -> {
                    //handle successful purchase
                    //mainly grant reward and navigate to next frag
                    findNavController().navigate(R.id.action_supportFragment_to_gratitudeFragment)
                }
            }
        }

        collectFlow(viewModel.state) { (list, isLoading) ->
            binding.recyclerView.apply {
                //sets spacing between items, NOT on start and end (horizontal)
                setItemSpacingPx(12)
                withModels {
                    spanCount = 2
                    supportText {
                        id("info")
                        //sets the span to fill the screen, spanSize = 2 (out of span count2)
                        spanSizeOverride { totalSpanCount, _, _ ->
                            totalSpanCount
                        }
                    }

                    //Loading state with progress bar
                    binding.progressBar.apply {
                        if (isLoading) show() else hide()
                    }

                    if (list.isEmpty()) {
                        localImageInfo {
                            id("error")
                            infoText("Looks like something went wrong. Try again later.")
                            spanSizeOverride { totalSpanCount, _, _ ->
                                totalSpanCount
                            }
                        }
                    }

                    list.forEach {
                        supportTierItem {
                            id(it.price)
                            price(it.price)
                            colorInt(it.color)
                            tierText(it.title)
                            desc(it.description)
                            onItemClicked {
                                billingWrapper.purchase(requireActivity(), it.skuDetails)
                            }
                        }
                    }
                }
            }
        }
         */
    }
}