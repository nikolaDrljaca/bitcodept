package com.drbrosdev.qrscannerfromlib.ui.codedetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.drbrosdev.qrscannerfromlib.database.QRCodeEntity
import com.drbrosdev.qrscannerfromlib.repo.CodeRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch
import logcat.logcat

class CodeDetailViewModel(
    val savedStateHandle: SavedStateHandle,
    private val repo: CodeRepository
): ViewModel() {
    private val loading = MutableStateFlow(true)

    private val _codeId = savedStateHandle.getLiveData<Int>("code_id").asFlow()

    private val code = _codeId
        .transform {
            emit(repo.fetchCodeById(it))
            loading.value = false
        }
        .onStart { loading.value = true }

    val state = combine(code, loading) { code, isLoading ->
        CodeDetailUiModel(
            isLoading = isLoading,
            code = code
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), CodeDetailUiModel())

    private val desc = MutableStateFlow("")

    private val updateDescJob = desc
        .debounce(500)
        .filterNotNull()
        .onEach { description ->
            val updatedCode = state.value.code?.copy(desc = description)
            updatedCode?.let { uCode ->
                repo.updateCodes(uCode)
            }
        }
        .launchIn(viewModelScope)

    private val _events = Channel<CodeDetailEvents>()
    val events = _events.receiveAsFlow()

    fun onDescriptionChanged(newValue: String) {
        desc.update { newValue }
    }

    fun sendThrownError(message: String) = viewModelScope.launch {
        _events.send(CodeDetailEvents.ShowThrownErrorMessage(message))
    }
}