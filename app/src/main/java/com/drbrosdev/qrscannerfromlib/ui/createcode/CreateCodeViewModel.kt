package com.drbrosdev.qrscannerfromlib.ui.createcode

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drbrosdev.qrscannerfromlib.database.QRCodeEntity
import com.drbrosdev.qrscannerfromlib.repo.CodeRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class CreateCodeViewModel(
    private val repo: CodeRepository
): ViewModel() {
    private val _events = Channel<CreateCodeEvents>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun insertCode(code: QRCodeEntity) = viewModelScope.launch {
        val result = repo.insertCode(code)
        if (result != 0L) {
            _events.send(CreateCodeEvents.ShowCodeSaved)
        }
    }

    fun showLoading() = viewModelScope.launch {
        _events.send(CreateCodeEvents.ShowLoading)
    }

    fun sendErrorEvent() = viewModelScope.launch {
        _events.send(CreateCodeEvents.ShowError)
    }
}