package com.drbrosdev.qrscannerfromlib.ui.createcode.createsheet

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drbrosdev.qrscannerfromlib.database.QRCodeEntity
import com.drbrosdev.qrscannerfromlib.model.QRCodeModel
import com.drbrosdev.qrscannerfromlib.repo.CodeRepository
import com.drbrosdev.qrscannerfromlib.ui.createcode.CodeType
import com.drbrosdev.qrscannerfromlib.ui.createcode.CreateCodeEvents
import com.drbrosdev.qrscannerfromlib.util.QRGenUtils
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class CreateCodeBottomViewModel(
    private val repo: CodeRepository,
    val stateHandle: SavedStateHandle
): ViewModel() {

    var firstField = stateHandle.get<String>("field_1") ?: ""
        set(value) {
            field = value
            stateHandle.set("field_1", value)
        }

    var secondField = stateHandle.get<String>("field_2") ?: ""
        set(value) {
            field = value
            stateHandle.set("field_2", value)
        }

    var thirdField = stateHandle.get<String>("field_3") ?: ""
        set(value) {
            field = value
            stateHandle.set("field_3", value)
        }

    private val _events = Channel<CreateCodeEvents>()
    val events = _events.receiveAsFlow()

    fun saveCode(codeType: CodeType) = viewModelScope.launch {
        _events.send(CreateCodeEvents.ShowLoading)
        val code = createCode(codeType)
        val id = repo.insertCode(code)
        if (id != 0L) {
            _events.send(CreateCodeEvents.ShowCodeSaved)
            delay(1200)
            _events.send(CreateCodeEvents.CompleteAndNavigateUp)
        }
    }

    private fun createCode(codeType: CodeType): QRCodeEntity {
        return when(codeType) {
            CodeType.URL -> {
                QRCodeEntity(
                    data = QRCodeModel.UrlModel(
                        rawValue = QRGenUtils.generateRawUrl(firstField),
                        title = "",
                        link = firstField
                    ),
                    userCreated = 1
                )
            }
            CodeType.SMS -> {
                QRCodeEntity(
                    data = QRCodeModel.SmsModel(
                        rawValue = QRGenUtils.generateRawSms(firstField, secondField),
                        message = secondField,
                        phoneNumber = firstField
                    ),
                    userCreated = 1
                )
            }
            CodeType.EMAIL -> {
                QRCodeEntity(
                    data = QRCodeModel.EmailModel(
                        rawValue = QRGenUtils.generateRawEmail(firstField, secondField, thirdField),
                        address = firstField,
                        subject = secondField,
                        body = thirdField
                    ),
                    userCreated = 1
                )
            }
            CodeType.PHONE -> {
                QRCodeEntity(
                    data = QRCodeModel.PhoneModel(
                        rawValue = firstField,
                        number = firstField
                    ),
                    userCreated = 1
                )
            }
            CodeType.WIFI -> {
                QRCodeEntity(
                    data = QRCodeModel.WifiModel(
                        rawValue = QRGenUtils.generateRawWifi(firstField, secondField),
                        ssid = firstField,
                        password = secondField
                    ),
                    userCreated = 1
                )
            }
            CodeType.PLAIN -> {
                QRCodeEntity(
                    data = QRCodeModel.PlainModel(
                        rawValue = firstField
                    ),
                    userCreated = 1
                )
            }
            CodeType.CONTACT -> {
                QRCodeEntity(
                    data = QRCodeModel.ContactInfoModel(
                        rawValue = QRGenUtils.generateContact(firstField, thirdField, secondField),
                        name = firstField,
                        email = secondField,
                        phone = thirdField
                    ),
                    userCreated = 1
                )
            }
        }
    }
}