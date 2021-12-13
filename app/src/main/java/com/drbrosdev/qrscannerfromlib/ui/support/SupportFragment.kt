package com.drbrosdev.qrscannerfromlib.ui.support

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.SkuDetailsParams
import com.android.billingclient.api.SkuDetailsResult
import com.android.billingclient.api.consumePurchase
import com.android.billingclient.api.querySkuDetails
import com.drbrosdev.qrscannerfromlib.R
import com.drbrosdev.qrscannerfromlib.databinding.FragmentSupportBinding
import com.drbrosdev.qrscannerfromlib.ui.epoxy.localImageInfo
import com.drbrosdev.qrscannerfromlib.ui.epoxy.supportText
import com.drbrosdev.qrscannerfromlib.ui.epoxy.supportTierItem
import com.drbrosdev.qrscannerfromlib.util.collectFlow
import com.drbrosdev.qrscannerfromlib.util.getCodeColorListAsMap
import com.drbrosdev.qrscannerfromlib.util.getColor
import com.drbrosdev.qrscannerfromlib.util.showShortToast
import com.drbrosdev.qrscannerfromlib.util.updateWindowInsets
import com.google.android.material.transition.MaterialSharedAxis
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class SupportFragment : Fragment(R.layout.fragment_support) {
    private val viewModel: SupportViewModel by viewModels()
    private val purchasesUpdatedListener =
        PurchasesUpdatedListener { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                viewModel.sendPurchases(purchases)

            } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
                //showShortToast("Something went wrong. Try again later.")
            } else {
                viewModel.sendErrorEvent()
            }
        }

    private val billingClient: BillingClient by inject { parametersOf(purchasesUpdatedListener) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        billingClient.queryPurchasesAsync(BillingClient.SkuType.INAPP) { p0, p1 ->
            if (p0.responseCode == BillingClient.BillingResponseCode.OK) {
                viewModel.sendPurchases(p1)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        billingClient.queryPurchasesAsync(BillingClient.SkuType.INAPP) { p0, p1 ->
            if (p0.responseCode == BillingClient.BillingResponseCode.OK) {
                viewModel.sendPurchases(p1)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().window.statusBarColor = getColor(R.color.background)

        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)

        val binding = FragmentSupportBinding.bind(view)
        updateWindowInsets(binding.root)

        viewModel.setLoading()
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingServiceDisconnected() {
                //Something has failed, try to restart the connection.
            }

            override fun onBillingSetupFinished(p0: BillingResult) {
                viewLifecycleOwner.lifecycleScope.launch {
                    val skuDetailsResult = querySkuDetails().skuDetailsList
                    skuDetailsResult?.let { viewModel.setSkuDetails(it, getCodeColorListAsMap()) }
                }
            }

        })

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
                                billingClient.launchBillingFlow(
                                    requireActivity(),
                                    BillingFlowParams.newBuilder().setSkuDetails(it.skuDetails)
                                        .build()
                                )
                            }
                        }
                    }
                }
            }
        }

        collectFlow(viewModel.events) {
            when(it) {
                SupportEvents.SendErrorToast -> {
                    showShortToast("Something went wrong. Try again later.")
                }
                is SupportEvents.SendPurchases -> {
                    viewLifecycleOwner.lifecycleScope.launch {
                        it.purchases.forEach { purchase ->  handlePurchase(purchase) }
                    }
                }
            }
        }
    }

    suspend fun querySkuDetails(): SkuDetailsResult {
        val skuList = mutableListOf<String>().apply {
            add("bitcodept_item_1")
            add("bitcodept_item_2")
            add("bitcodept_item_3")
            add("bitcodept_item_4")
        }
        val params = SkuDetailsParams.newBuilder()
            .setSkusList(skuList).setType(BillingClient.SkuType.INAPP)

        val result = withContext(Dispatchers.IO) {
            billingClient.querySkuDetails(params.build())
        }

        return result
    }

    private suspend fun handlePurchase(p: Purchase) {
        if (p.purchaseState == Purchase.PurchaseState.PURCHASED) {
            findNavController().navigate(R.id.action_supportFragment_to_gratitudeFragment)
        }

        val consumeParams = ConsumeParams.newBuilder()
            .setPurchaseToken(p.purchaseToken)
            .build()
        withContext(Dispatchers.IO) {
            val result = billingClient.consumePurchase(consumeParams)
        }
    }
}