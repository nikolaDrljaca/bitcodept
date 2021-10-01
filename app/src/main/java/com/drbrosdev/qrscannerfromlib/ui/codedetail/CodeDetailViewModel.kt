package com.drbrosdev.qrscannerfromlib.ui.codedetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drbrosdev.qrscannerfromlib.repo.CodeRepository
import com.drbrosdev.qrscannerfromlib.util.setState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class CodeDetailViewModel(
    val savedStateHandle: SavedStateHandle,
    private val repo: CodeRepository
): ViewModel() {
    private val _viewState = MutableStateFlow(CodeDetailViewState())
    val viewState = _viewState.asStateFlow()

    private val codeId = savedStateHandle.get<Int>("code_id")

    private val _events = Channel<CodeDetailEvents>()
    val events = _events.receiveAsFlow()

    init { getCode() }

    private fun getCode() = viewModelScope.launch {
        codeId?.let {
            repo.getCodeById(it).collect { code ->
                _viewState.setState { copy(_code = code) }
            }
        }
    }

    fun sendThrownError(message: String) = viewModelScope.launch {
        _events.send(CodeDetailEvents.ShowThrownErrorMessage(message))
    }
}