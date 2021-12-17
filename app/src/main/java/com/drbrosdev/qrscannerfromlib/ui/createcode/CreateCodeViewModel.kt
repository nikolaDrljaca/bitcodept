package com.drbrosdev.qrscannerfromlib.ui.createcode

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drbrosdev.qrscannerfromlib.database.QRCodeEntity
import com.drbrosdev.qrscannerfromlib.datastore.AppPreferences
import com.drbrosdev.qrscannerfromlib.model.QRCodeModel
import com.drbrosdev.qrscannerfromlib.repo.CodeRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class CreateCodeViewModel(
    private val repo: CodeRepository,
    private val prefs: AppPreferences,
    val savedStateHandle: SavedStateHandle,
): ViewModel() {
    private val _events = Channel<CreateCodeEvents>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    var codeText = savedStateHandle.get<String>("code_text") ?: ""
        set(value) {
            field = value
            savedStateHandle.set("code_text", value)
        }

    private fun insertCode(code: QRCodeEntity) = viewModelScope.launch {
        val result = repo.insertCode(code)
        if (result != 0L) {
            prefs.incrementSupportKey()
            _events.send(CreateCodeEvents.ShowCodeSaved)
        }
    }

    private fun showLoading() = viewModelScope.launch {
        _events.send(CreateCodeEvents.ShowLoading)
    }

    private fun sendEmptyTextEvent() = viewModelScope.launch {
        _events.send(CreateCodeEvents.CodeTextIsEmpty)
    }

    fun createCode() {
        if (codeText.isBlank()) {
            sendEmptyTextEvent()
            return
        }
        showLoading()
        val code = QRCodeEntity(
            data = QRCodeModel.PlainModel(codeText),
            userCreated = 1
        )
        insertCode(code)
    }
}