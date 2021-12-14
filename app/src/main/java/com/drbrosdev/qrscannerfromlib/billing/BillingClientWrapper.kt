package com.drbrosdev.qrscannerfromlib.billing

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.SkuDetails
import com.android.billingclient.api.SkuDetailsParams
import com.android.billingclient.api.SkuDetailsResponseListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class BillingClientWrapper(context: Context) : PurchasesUpdatedListener {
    /*
    Creating a scope here is fine, BUT KEEP IN MIND this class will have a singleton object
    which is used in the HomeFrag and SupportFrag, because pending purchases need to be consumed in order
    to not get refunded(3 day limit).
    This means that the scope is alive as long as the object and thus any work its doing inside the
    .launch{} blocks will persist. The ideal thing to do is to clear a scope once the fragments can
    no longer receive any data and invoke UI operations(will cause runtime crashes).

    Since here the only thing we are doing is sending events to a flow there should not be any overlapping
    issues as this is a "fast" processes.
     */
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private val _productsFlow = Channel<QueriedProducts>()
    val productsFlow = _productsFlow.receiveAsFlow()

    private val _purchaseFlow = Channel<PurchaseResult>()
    val purchaseFlow = _purchaseFlow.receiveAsFlow()

    private val billingClient = BillingClient.newBuilder(context)
        .enablePendingPurchases()
        .setListener(this)
        .build()

    override fun onPurchasesUpdated(p0: BillingResult, p1: MutableList<Purchase>?) {
        when(p0.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                if (p1 == null) {
                    //?? questionable functionality, maybe not needed to emit to ui
                    scope.launch { _purchaseFlow.send(PurchaseResult.Success(null)) }
                    return
                }
                p1.forEach(::processPurchase)
            }
            else -> {
                scope.launch {
                    _purchaseFlow.send(PurchaseResult.Failure(
                        errorMessage = "Something went wrong.",
                        debug = p0.debugMessage
                    ))
                }
            }
        }
    }

    fun purchase(activity: Activity, product: SkuDetails) {
        onConnected {
            activity.runOnUiThread {
                //LaunchBillingFlow has no callback, it invokes onPurchasesUpdated above
                billingClient.launchBillingFlow(
                    activity,
                    BillingFlowParams.newBuilder().setSkuDetails(product).build()
                )
            }
        }
    }

    fun queryProducts() {
        val skuList = listOf("bitcodept_item_1","bitcodept_item_2","bitcodept_item_3","bitcodept_item_4")
        queryInAppProducts(skuList) { billingResult, skuDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                skuDetailsList?.let {
                    scope.launch { _productsFlow.send(QueriedProducts.Success(it)) }
                }
            } else {
                scope.launch {
                    _productsFlow.send(QueriedProducts.Failure(
                        errorMessage = "Something went wrong.",
                        debug = billingResult.debugMessage
                    ))
                }
            }
        }
    }

    fun retryToConsumePurchases() {
        billingClient.queryPurchasesAsync(BillingClient.SkuType.INAPP) { p0, p1 ->
            if (p0.responseCode == BillingClient.BillingResponseCode.OK) {
                 p1.forEach(::processPurchase)
            }
        }
    }

    private fun processPurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            scope.launch { _purchaseFlow.send(PurchaseResult.Success(purchase)) }

            onConnected {
                billingClient.consumeAsync(
                    ConsumeParams.newBuilder().setPurchaseToken(purchase.purchaseToken).build()
                ) { billingResult, token ->
                    //save the token or whatever
                    if (billingResult.responseCode != BillingClient.BillingResponseCode.OK) {
                        //implement retry logic or try to consume again in onResume()->fragment
                    }
                }
            }
        }
    }

    private fun queryInAppProducts(skuList: List<String>, listener: SkuDetailsResponseListener) {
        onConnected {
            billingClient.querySkuDetailsAsync(
                SkuDetailsParams.newBuilder().setSkusList(skuList)
                    .setType(BillingClient.SkuType.INAPP).build(),
                listener
            )
        }
    }

    private fun onConnected(block: () -> Unit) {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingServiceDisconnected() {
                //TODO("Not yet implemented")
            }

            override fun onBillingSetupFinished(p0: BillingResult) {
                block()
            }
        })
    }
}

sealed interface QueriedProducts {
    data class Success(val products: List<SkuDetails>) : QueriedProducts
    data class Failure(val errorMessage: String, val debug: String) : QueriedProducts
}

sealed interface PurchaseResult {
    data class Success(val purchase: Purchase?): PurchaseResult
    data class Failure(val errorMessage: String, val debug: String): PurchaseResult
}