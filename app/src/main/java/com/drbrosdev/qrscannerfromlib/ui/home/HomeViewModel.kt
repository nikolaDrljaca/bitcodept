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
    //stateFlow as state holder class for fragment
    private val _viewState = MutableStateFlow(HomeViewState())
    val viewState = _viewState.asStateFlow()

    //channel for one-shot events and notifications to the fragment
    private val _eventChannel = Channel<HomeEvents>()
    val eventChannel = _eventChannel.receiveAsFlow()

    val showReviewCount = prefs.showInAppReviewRequest

    init {
        getCodes()
    }

    fun incrementStartupCount() = viewModelScope.launch {
        prefs.incrementReviewKey()
    }

    fun deleteAllCodes() = viewModelScope.launch {
        if (!_viewState.value.isEmpty)
            repo.deleteAllCodes()
    }

    fun deleteCode(code: QRCodeEntity) = viewModelScope.launch {
        try {
            repo.deleteCode(code)
            _eventChannel.send(HomeEvents.ShowUndoCodeDelete(code))
        } catch (e: Exception) {
            _viewState.setState { copy(_codeList = AsyncModel.Fail(e)) }
        }
    }

    fun insertCode(code: QRCodeEntity) = viewModelScope.launch {
        _eventChannel.send(HomeEvents.ShowSavingCode)
        try {
            val result = repo.insertCode(code)
            val id = Integer.parseInt(result.toString())
            _eventChannel.send(HomeEvents.ShowCurrentCodeSaved(id))
        } catch (e: Exception) {
            _viewState.setState { copy(_codeList = AsyncModel.Fail(e)) }
        }
    }

    fun undoDelete(code: QRCodeEntity) = viewModelScope.launch {
        try {
            repo.insertCode(code)
        } catch (e: Exception) {
            _viewState.setState { copy(_codeList = AsyncModel.Fail(e)) }
        }
    }

    fun sendSavingEvent() = viewModelScope.launch { _eventChannel.send(HomeEvents.ShowSavingCode) }

    fun sendErrorImageEvent() = viewModelScope.launch {
        _eventChannel.send(HomeEvents.ShowErrorCreatingCodeImage)
    }

    private fun getCodes() = viewModelScope.launch {
        repo.getCodes().collect { codes ->
            _viewState.setState { copy(_codeList = codes) }
        }
    }
}