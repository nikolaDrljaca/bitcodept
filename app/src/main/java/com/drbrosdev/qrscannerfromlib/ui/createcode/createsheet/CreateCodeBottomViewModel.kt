package com.drbrosdev.qrscannerfromlib.ui.createcode.createsheet

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drbrosdev.qrscannerfromlib.database.QRCodeEntity
import com.drbrosdev.qrscannerfromlib.model.QRCodeModel
import com.drbrosdev.qrscannerfromlib.repo.CodeRepository
import com.drbrosdev.qrscannerfromlib.util.QRGenUtils
import kotlinx.coroutines.launch

class CreateCodeBottomViewModel(
    private val repo: CodeRepository,
    val stateHandle: SavedStateHandle
): ViewModel() {

    var number = stateHandle.get<String>("number_text") ?: ""
        set(value) {
            field = value
            stateHandle.set("number_text", value)
        }

    var message = stateHandle.get<String>("message_text") ?: ""
        set(value) {
            field = value
            stateHandle.set("message_text", value)
        }

    fun createCode() {
        val code = QRCodeEntity(
            data = QRCodeModel.SmsModel(
                rawValue = QRGenUtils.generateRawSms(number, message),
                message = message,
                phoneNumber = number
            ),
            userCreated = 1
        )
        viewModelScope.launch {
            repo.insertCode(code)
        }
    }
}