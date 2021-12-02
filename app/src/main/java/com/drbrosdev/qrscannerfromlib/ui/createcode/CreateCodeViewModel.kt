package com.drbrosdev.qrscannerfromlib.ui.createcode

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drbrosdev.qrscannerfromlib.database.QRCodeEntity
import com.drbrosdev.qrscannerfromlib.datastore.AppPreferences
import com.drbrosdev.qrscannerfromlib.model.QRCodeModel
import com.drbrosdev.qrscannerfromlib.network.CreateQRCodeRequest
import com.drbrosdev.qrscannerfromlib.repo.CodeRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class CreateCodeViewModel(
    private val repo: CodeRepository,
    private val prefs: AppPreferences,
): ViewModel() {
    private val _events = Channel<CreateCodeEvents>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private val requester = CreateQRCodeRequest()

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

    private fun sendErrorEvent() = viewModelScope.launch {
        _events.send(CreateCodeEvents.ShowError)
    }

    private fun sendEmptyTextEvent() = viewModelScope.launch {
        _events.send(CreateCodeEvents.CodeTextIsEmpty)
    }

    fun createCode(codeContent: String, colorInt: Int) {
        if (codeContent.isBlank()) {
            sendEmptyTextEvent()
            return
        }
        showLoading()
        requester.createCall(codeContent, colorInt, toThrow = true)
            .catch { sendErrorEvent() }
            .onEach {
                val code = QRCodeEntity(
                    data = QRCodeModel.PlainModel(codeContent),
                    codeImage = it,
                    userCreated = 1
                )
                insertCode(code)
            }
            .launchIn(viewModelScope)
    }
}