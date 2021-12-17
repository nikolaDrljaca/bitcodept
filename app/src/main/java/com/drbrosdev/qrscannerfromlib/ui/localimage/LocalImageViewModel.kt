package com.drbrosdev.qrscannerfromlib.ui.localimage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drbrosdev.qrscannerfromlib.database.QRCodeEntity
import com.drbrosdev.qrscannerfromlib.datastore.AppPreferences
import com.drbrosdev.qrscannerfromlib.model.QRCodeModel
import com.drbrosdev.qrscannerfromlib.repo.CodeRepository
import com.google.mlkit.vision.barcode.Barcode
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class LocalImageViewModel(
    private val repo: CodeRepository,
    private val prefs: AppPreferences
) : ViewModel() {
    private val localListOfCodes = mutableListOf<QRCodeEntity>()

    private val codes = MutableLiveData<List<QRCodeEntity>>(emptyList())
    private val errorMessage = MutableLiveData("")

    private val _events = Channel<LocalImageEvents>()
    val events = _events.receiveAsFlow()

    val state: LiveData<LocalImageViewState> = MediatorLiveData<LocalImageViewState>().also { mediator ->
        mediator.value = LocalImageViewState()

        mediator.addSource(codes) {
            val (_, message) = mediator.value!!
            mediator.value = LocalImageViewState(codes = it, message)
        }

        mediator.addSource(errorMessage) {
            val (list, _) = mediator.value!!
            mediator.value = LocalImageViewState(list, it)
        }
    }

    fun submitError(message: String) = viewModelScope.launch {
        errorMessage.value = message
    }

    fun deleteLocalDetectedCode(code: QRCodeEntity) = viewModelScope.launch {
        repo.deleteCode(code)
        localListOfCodes.remove(code)
        codes.postValue(localListOfCodes)
    }

    private fun pushCode(entity: QRCodeEntity) {
        viewModelScope.launch {
            val id = repo.insertCode(entity)
            prefs.incrementSupportKey()
            localListOfCodes.add(repo.fetchCodeById(id.toInt()))
            codes.postValue(localListOfCodes)
        }
    }

    fun submitBarcodes(barcodes: List<Barcode>) {
        viewModelScope.launch {
            if (barcodes.isEmpty()) _events.send(LocalImageEvents.ShowEmptyResult)
            else _events.send(LocalImageEvents.ShowLoading)
        }
        localListOfCodes.clear()
        codes.value = localListOfCodes
        barcodes.forEach { barcode ->
            when (barcode.valueType) {
                Barcode.TYPE_CONTACT_INFO -> {
                    val entity = QRCodeEntity(
                        data = QRCodeModel.ContactInfoModel(
                            rawValue = barcode.rawValue ?: "",
                            name = barcode.contactInfo?.name?.formattedName!!,
                            email = barcode.contactInfo?.emails?.first()?.address ?: "",
                            phone = barcode.contactInfo?.phones?.first()?.number ?: ""
                        ),
                        userCreated = 2
                    )
                    pushCode(entity)
                }
                Barcode.TYPE_EMAIL -> {
                    val entity = QRCodeEntity(
                        data = QRCodeModel.EmailModel(
                            rawValue = barcode.rawValue ?: "",
                            address = barcode.email?.address ?: "",
                            body = barcode.email?.body ?: "",
                            subject = barcode.email?.subject ?: "",
                        ),
                        userCreated = 2
                    )
                    pushCode(entity)

                }
                Barcode.TYPE_PHONE -> {
                    val entity = QRCodeEntity(
                        data = QRCodeModel.PhoneModel(
                            rawValue = barcode.rawValue ?: "",
                            number = barcode.phone?.number ?: ""
                        ),
                        userCreated = 2
                    )
                    pushCode(entity)
                }
                Barcode.TYPE_SMS -> {
                    val entity = QRCodeEntity(
                        data = QRCodeModel.SmsModel(
                            rawValue = barcode.rawValue ?: "",
                            message = barcode.sms?.message ?: "",
                            phoneNumber = barcode.sms?.phoneNumber ?: "",
                        ),
                        userCreated = 2
                    )
                    pushCode(entity)
                }
                Barcode.TYPE_TEXT -> {
                    val entity = QRCodeEntity(
                        data = QRCodeModel.PlainModel(
                            rawValue = barcode.rawValue ?: ""
                        ),
                        userCreated = 2
                    )
                    pushCode(entity)

                }
                Barcode.TYPE_URL -> {
                    val entity = QRCodeEntity(
                        data = QRCodeModel.UrlModel(
                            rawValue = barcode.rawValue ?: "",
                            link = barcode.url?.url ?: "",
                            title = barcode.url?.title ?: ""
                        ),
                        userCreated = 2
                    )
                    pushCode(entity)
                }
                Barcode.TYPE_GEO -> {
                    val entity = QRCodeEntity(
                        data = QRCodeModel.GeoPointModel(
                            rawValue = barcode.rawValue ?: "",
                            lat = barcode.geoPoint?.lat ?: 0.0,
                            lng = barcode.geoPoint?.lng ?: 0.0
                        ),
                        userCreated = 2
                    )
                    pushCode(entity)
                }
                Barcode.TYPE_WIFI -> {
                    val entity = QRCodeEntity(
                        data = QRCodeModel.WifiModel(
                            rawValue = barcode.rawValue ?: "",
                            ssid = barcode.wifi?.ssid ?: "",
                            password = barcode.wifi?.password ?: ""
                        ),
                        userCreated = 2
                    )
                    pushCode(entity)
                }
            }
        }
    }
}