package com.drbrosdev.qrscannerfromlib.ui.support

/*
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
 */