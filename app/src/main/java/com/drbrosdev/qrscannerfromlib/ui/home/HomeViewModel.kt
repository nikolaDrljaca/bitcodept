package com.drbrosdev.qrscannerfromlib.ui.home

import androidx.lifecycle.*
import com.drbrosdev.qrscannerfromlib.database.QRCodeEntity
import com.drbrosdev.qrscannerfromlib.datastore.AppPreferences
import com.drbrosdev.qrscannerfromlib.repo.CodeRepository
import com.drbrosdev.qrscannerfromlib.util.AsyncModel
import com.drbrosdev.qrscannerfromlib.util.setState
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*

class HomeViewModel(
    private val repo: CodeRepository,
    private val prefs: AppPreferences
) : ViewModel() {
    //channel for one-shot events and notifications to the fragment
    private val _eventChannel = Channel<HomeEvents>()
    val eventChannel = _eventChannel.receiveAsFlow()

    private val loading = MutableStateFlow(true)

    private val codes = repo.listOfCodes
        .distinctUntilChanged()
        .onEach { loading.value = false }
        .onStart { loading.value = true }
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed())

    private val listOfCodes = codes
        .map { list ->
            list.filter { it.userCreated == 0 || it.userCreated == 2 }
        }

    private val listOfUserCodes = codes
        .map { list ->
            list.filter { it.userCreated == 1 }
        }

    val state = combine(
        loading,
        listOfCodes,
        listOfUserCodes
    ) { isLoading, listOfCodes, listOfUserCodes ->
        HomeUiModel(
            isLoading = isLoading,
            codeList = listOfCodes,
            userCodeList = listOfUserCodes
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), HomeUiModel())

    val showReviewCount = prefs.showInAppReviewRequest

    init {
        showFirstUpdate()
    }

    fun incrementStartupCount() = viewModelScope.launch {
        prefs.incrementReviewKey()
    }

    fun deleteAllCodes() = viewModelScope.launch {
        if (!state.value.isEmpty)
            repo.deleteAllCodes()
    }

    fun deleteCode(code: QRCodeEntity) = viewModelScope.launch {
        repo.deleteCode(code)
        _eventChannel.send(HomeEvents.ShowUndoCodeDelete(code))
    }

    fun insertCode(code: QRCodeEntity) = viewModelScope.launch {
        _eventChannel.send(HomeEvents.ShowSavingCode)
        val result = repo.insertCode(code)
        val id = Integer.parseInt(result.toString())
        _eventChannel.send(HomeEvents.ShowCurrentCodeSaved(id))
    }

    fun undoDelete(code: QRCodeEntity) = viewModelScope.launch {
        repo.insertCode(code)
    }

    fun sendSavingEvent() = viewModelScope.launch { _eventChannel.send(HomeEvents.ShowSavingCode) }

    fun sendErrorImageEvent() = viewModelScope.launch {
        _eventChannel.send(HomeEvents.ShowErrorCreatingCodeImage)
    }

    /*
    If the user is booting up the app for the first time, don't show the feature update and mark it
    as shown so it doesn't present itself on next boot.

    If the user has already seen the onboarding activity, show the dialog.
     */
    private fun showFirstUpdate() = viewModelScope.launch {
        val hasSeen = prefs.hasSeenFirstUpdate.first()
        val firstLaunch = prefs.isFirstLaunch.first()
        if (firstLaunch && !hasSeen) {
            _eventChannel.send(HomeEvents.ShowFirstUpdateDialog)
            prefs.seenFirstUpdateComplete()
        } else {
            prefs.seenFirstUpdateComplete()
        }
    }
}