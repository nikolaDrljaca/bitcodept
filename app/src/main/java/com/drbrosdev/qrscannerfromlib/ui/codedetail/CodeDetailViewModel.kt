package com.drbrosdev.qrscannerfromlib.ui.codedetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.drbrosdev.qrscannerfromlib.repo.CodeRepository
import com.drbrosdev.qrscannerfromlib.ui.home.HomeUiModel
import com.drbrosdev.qrscannerfromlib.util.setState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CodeDetailViewModel(
    val savedStateHandle: SavedStateHandle,
    private val repo: CodeRepository
): ViewModel() {
    private val loading = MutableStateFlow(true)

    private val _codeId = savedStateHandle.getLiveData<Int>("code_id").asFlow()

    private val code = _codeId
        .map { repo.fetchCodeById(it) }
        .onEach { loading.value = false }

    val state = combine(code, loading) { code, isLoading ->
        CodeDetailUiModel(
            isLoading = isLoading,
            code = code
        )
    }

    private val _events = Channel<CodeDetailEvents>()
    val events = _events.receiveAsFlow()

    fun sendThrownError(message: String) = viewModelScope.launch {
        _events.send(CodeDetailEvents.ShowThrownErrorMessage(message))
    }
}