package com.drbrosdev.qrscannerfromlib.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drbrosdev.qrscannerfromlib.database.QRCodeEntity
import com.drbrosdev.qrscannerfromlib.datastore.AppPreferences
import com.drbrosdev.qrscannerfromlib.model.QRCodeModel
import com.drbrosdev.qrscannerfromlib.network.CreateQRCodeRequest
import com.drbrosdev.qrscannerfromlib.repo.CodeRepository
import io.github.g00fy2.quickie.content.QRContent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import logcat.logcat

class HomeViewModel(
    private val repo: CodeRepository,
    private val prefs: AppPreferences
) : ViewModel() {
    //channel for one-shot events and notifications to the fragment
    private val _eventChannel = Channel<HomeEvents>(capacity = 1)
    val eventChannel = _eventChannel.receiveAsFlow()

    private val loading = MutableStateFlow(true)
    private val codeItemHeight = MutableStateFlow(0)

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
        listOfUserCodes,
        codeItemHeight
    ) { isLoading, listOfCodes, listOfUserCodes, height ->
        HomeUiModel(
            isLoading = isLoading,
            codeList = listOfCodes,
            userCodeList = listOfUserCodes,
            codeItemHeight = height
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), HomeUiModel())

    private val showSupportCount = prefs.showSupport
        .onEach {
            logcat { "$it" }
            if (it % 6 == 0) {
                _eventChannel.send(HomeEvents.ShowSupportDialog)
                codeScanIncrementSupportKey()
            }
        }
        .launchIn(viewModelScope)

    fun emitCodeItemHeight(value: Flow<Int>) {
        value
            .onEach { codeItemHeight.value = it }
            .launchIn(viewModelScope)
    }

    private fun codeScanIncrementSupportKey() = viewModelScope.launch {
        prefs.incrementSupportKey()
    }

    fun deleteAllCodes() = viewModelScope.launch {
        if (!state.value.isEmpty)
            repo.deleteAllCodes()
    }

    fun deleteCode(code: QRCodeEntity) = viewModelScope.launch {
        repo.deleteCode(code)
        _eventChannel.send(HomeEvents.ShowUndoCodeDelete(code))
    }

    private fun insertCode(code: QRCodeEntity) = viewModelScope.launch {
        _eventChannel.send(HomeEvents.ShowSavingCode)
        val result = repo.insertCode(code)
        val id = Integer.parseInt(result.toString())
        _eventChannel.send(HomeEvents.ShowCurrentCodeSaved(id)).also {
            /*
            Increment key to show support dialog when sending the event.
            This will increase the value without triggering the onEach instantly.
             */
            codeScanIncrementSupportKey()
        }
    }

    fun undoDelete(code: QRCodeEntity) = viewModelScope.launch {
        repo.insertCode(code)
    }

    private fun sendSavingEvent() = viewModelScope.launch { _eventChannel.send(HomeEvents.ShowSavingCode) }

    private fun sendErrorImageEvent() = viewModelScope.launch {
        _eventChannel.send(HomeEvents.ShowErrorCreatingCodeImage)
    }

    private val requester = CreateQRCodeRequest()

    fun handleResultContent(content: QRContent, colors: Map<String, Int>) = viewModelScope.launch {
        sendSavingEvent()
        when(content) {
            is QRContent.Plain -> {
                requester.createCall(content.rawValue, colors["text"] ?: 0)
                    .catch { sendErrorImageEvent() }
                    .collect {
                        val code = QRCodeEntity(
                            data = QRCodeModel.PlainModel(content.rawValue),
                            codeImage = it
                        )
                        insertCode(code)
                    }
            }
            is QRContent.Wifi -> {
                requester.createCall(content.rawValue, colors["wifi"] ?: 0)
                    .catch { sendErrorImageEvent() }
                    .collect {
                        val code = QRCodeEntity(
                            data = QRCodeModel.WifiModel(
                                rawValue = content.rawValue,
                                ssid = content.ssid,
                                password = content.password
                            ),
                            codeImage = it
                        )
                        insertCode(code)
                    }
            }
            is QRContent.Url -> {
                requester.createCall(content.rawValue, colors["url"] ?: 0)
                    .catch { sendErrorImageEvent() }
                    .collect {
                        val code = QRCodeEntity(
                            data = QRCodeModel.UrlModel(
                                rawValue = content.rawValue,
                                title = content.title,
                                link = content.url
                            ),
                            codeImage = it
                        )
                        insertCode(code)
                    }
            }
            is QRContent.Sms -> {
                requester.createCall(content.rawValue, colors["sms"] ?: 0)
                    .catch { sendErrorImageEvent() }
                    .collect {
                        val code = QRCodeEntity(
                            data = QRCodeModel.SmsModel(
                                rawValue = content.rawValue,
                                message = content.message,
                                phoneNumber = content.phoneNumber
                            ),
                            codeImage = it
                        )
                        insertCode(code)
                    }
            }
            is QRContent.GeoPoint -> {
                requester.createCall(content.rawValue, colors["geo"] ?: 0)
                    .catch { sendErrorImageEvent() }
                    .collect {
                        val code = QRCodeEntity(
                            data = QRCodeModel.GeoPointModel(
                                rawValue = content.rawValue,
                                lat = content.lat,
                                lng = content.lng
                            ), codeImage = it
                        )
                        insertCode(code)
                    }
            }
            is QRContent.Email -> {
                requester.createCall(content.rawValue, colors["email"] ?: 0)
                    .catch { sendErrorImageEvent() }
                    .collect {
                        val code = QRCodeEntity(
                            data = QRCodeModel.EmailModel(
                                rawValue = content.rawValue,
                                address = content.address,
                                body = content.body,
                                subject = content.subject
                            ), codeImage = it
                        )
                        insertCode(code)
                    }
            }
            is QRContent.Phone -> {
                requester.createCall(content.rawValue, colors["phone"] ?: 0)
                    .catch { sendErrorImageEvent() }
                    .collect {
                        val code = QRCodeEntity(
                            data = QRCodeModel.PhoneModel(
                                rawValue = content.rawValue,
                                number = content.number
                            ), codeImage = it
                        )
                        insertCode(code)
                    }
            }
            is QRContent.ContactInfo -> {
                requester.createCall(content.rawValue, colors["contact"] ?: 0)
                    .catch { sendErrorImageEvent() }
                    .collect {
                        val code = QRCodeEntity(
                            data = QRCodeModel.ContactInfoModel(
                                rawValue = content.rawValue,
                                name = content.name.formattedName,
                                email = content.emails.firstOrNull()?.address ?: " ",
                                phone = content.phones.firstOrNull()?.number ?: " "
                            ), codeImage = it
                        )
                        insertCode(code)
                    }
            }
            is QRContent.CalendarEvent -> {
                _eventChannel.send(HomeEvents.CalendarCodeEvent)
            }
        }
    }
}