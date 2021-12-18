package com.drbrosdev.qrscannerfromlib.ui.support

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.SkuDetails
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SupportViewModel : ViewModel() {
    private val _events = Channel<SupportEvents>()
    val events = _events.receiveAsFlow()

    private val supportList = MutableStateFlow(emptyList<SupportItem>())
    private val isLoading = MutableStateFlow(true)

    val state = combine(supportList, isLoading) { list, loading ->
        Pair(list, loading)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Pair(emptyList(), true))

    fun setSkuDetails(list: List<SkuDetails>, colors: Map<String, Int>) {
        supportList.value = list.mapIndexed { index, it ->
            SupportItem(
                skuDetails = it,
                color = colors.toList()[index].second
            )
        }
        isLoading.value = false
    }

    fun setFailure() {
        isLoading.value = false
        viewModelScope.launch { _events.send(SupportEvents.SendErrorToast) }
    }
}