package com.drbrosdev.qrscannerfromlib.ui.localimage

import androidx.lifecycle.*
import com.drbrosdev.qrscannerfromlib.database.QRCodeEntity
import com.drbrosdev.qrscannerfromlib.model.QRCodeModel
import com.drbrosdev.qrscannerfromlib.network.CreateQRCodeRequest
import com.drbrosdev.qrscannerfromlib.repo.CodeRepository
import com.google.mlkit.vision.barcode.Barcode
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class LocalImageViewModel(
    private val repo: CodeRepository
) : ViewModel() {
    private val requester = CreateQRCodeRequest()

    private val localListOfCodes = mutableListOf<QRCodeEntity>()

    private val codes = MutableLiveData<List<QRCodeEntity>>(emptyList())
    private val errorMessage = MutableLiveData("")

    private val _events = Channel<LocalImageEvents>()
    val events = _events.receiveAsFlow()

    val state: LiveData<LocalImageViewState> = MediatorLiveData<LocalImageViewState>().apply {
        value = LocalImageViewState()

        addSource(codes) {
            value = LocalImageViewState(codes = it)
        }
        addSource(errorMessage) {
            value = LocalImageViewState(errorMessage = it)
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
            localListOfCodes.add(repo.fetchCodeById(id.toInt()))
            codes.postValue(localListOfCodes)
        }
    }

    fun submitBarcodes(barcodes: List<Barcode>, colorList: Map<String, Int>) {
        viewModelScope.launch {
            if (barcodes.isEmpty()) _events.send(LocalImageEvents.ShowEmptyResult)
            else _events.send(LocalImageEvents.ShowLoading)
        }
        localListOfCodes.clear()
        codes.value = localListOfCodes
        barcodes.forEach { barcode ->
            when (barcode.valueType) {
                Barcode.TYPE_CONTACT_INFO -> {
                    requester.createCall(
                        codeContent = barcode.rawValue ?: "",
                        colorInt = colorList["contact"] ?: 0,
                        onImageLoaded = {
                            val entity = QRCodeEntity(
                                data = QRCodeModel.ContactInfoModel(
                                    rawValue = barcode.rawValue ?: "",
                                    name = barcode.contactInfo?.name?.formattedName!!,
                                    email = barcode.contactInfo?.emails?.first()?.address ?: "",
                                    phone = barcode.contactInfo?.phones?.first()?.number ?: ""
                                ),
                                codeImage = it,
                                userCreated = 2
                            )
                            pushCode(entity)
                        },
                        onFail = {
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
                    )
                }
                Barcode.TYPE_EMAIL -> {
                    requester.createCall(
                        codeContent = barcode.rawValue ?: "",
                        colorInt = colorList["email"] ?: 0,
                        onImageLoaded = {
                            val entity = QRCodeEntity(
                                data = QRCodeModel.EmailModel(
                                    rawValue = barcode.rawValue ?: "",
                                    address = barcode.email?.address ?: "",
                                    body = barcode.email?.body ?: "",
                                    subject = barcode.email?.subject ?: "",
                                ),
                                codeImage = it,
                                userCreated = 2
                            )
                            pushCode(entity)
                        },
                        onFail = {
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
                    )

                }
                Barcode.TYPE_PHONE -> {
                    requester.createCall(
                        codeContent = barcode.rawValue ?: "",
                        colorInt = colorList["phone"] ?: 0,
                        onImageLoaded = {
                            val entity = QRCodeEntity(
                                data = QRCodeModel.PhoneModel(
                                    rawValue = barcode.rawValue ?: "",
                                    number = barcode.phone?.number ?: ""
                                ),
                                codeImage = it,
                                userCreated = 2
                            )
                            pushCode(entity)
                        },
                        onFail = {
                            val entity = QRCodeEntity(
                                data = QRCodeModel.PhoneModel(
                                    rawValue = barcode.rawValue ?: "",
                                    number = barcode.phone?.number ?: ""
                                ),
                                userCreated = 2
                            )
                            pushCode(entity)
                        }
                    )
                }
                Barcode.TYPE_SMS -> {
                    requester.createCall(
                        codeContent = barcode.rawValue ?: "",
                        colorInt = colorList["sms"] ?: 0,
                        onImageLoaded = {
                            val entity = QRCodeEntity(
                                data = QRCodeModel.SmsModel(
                                    rawValue = barcode.rawValue ?: "",
                                    message = barcode.sms?.message ?: "",
                                    phoneNumber = barcode.sms?.phoneNumber ?: "",
                                ),
                                codeImage = it,
                                userCreated = 2
                            )
                            pushCode(entity)
                        },
                        onFail = {
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
                    )
                }
                Barcode.TYPE_TEXT -> {
                    requester.createCall(
                        codeContent = barcode.rawValue ?: "",
                        colorInt = colorList["text"] ?: 0,
                        onImageLoaded = {
                            val entity = QRCodeEntity(
                                data = QRCodeModel.PlainModel(
                                    rawValue = barcode.rawValue ?: ""
                                ),
                                codeImage = it,
                                userCreated = 2
                            )
                            pushCode(entity)
                        },
                        onFail = {
                            val entity = QRCodeEntity(
                                data = QRCodeModel.PlainModel(
                                    rawValue = barcode.rawValue ?: ""
                                ),
                                userCreated = 2
                            )
                            pushCode(entity)
                        }
                    )

                }
                Barcode.TYPE_URL -> {
                    requester.createCall(
                        codeContent = barcode.rawValue ?: "",
                        colorInt = colorList["url"] ?: 0,
                        onImageLoaded = {
                            val entity = QRCodeEntity(
                                data = QRCodeModel.UrlModel(
                                    rawValue = barcode.rawValue ?: "",
                                    link = barcode.url?.url ?: "",
                                    title = barcode.url?.title ?: ""
                                ),
                                codeImage = it,
                                userCreated = 2
                            )
                            pushCode(entity)
                        },
                        onFail = {
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
                    )
                }
                Barcode.TYPE_GEO -> {
                    requester.createCall(
                        codeContent = barcode.rawValue ?: "",
                        colorInt = colorList["geo"] ?: 0,
                        onImageLoaded = {
                            val entity = QRCodeEntity(
                                data = QRCodeModel.GeoPointModel(
                                    rawValue = barcode.rawValue ?: "",
                                    lat = barcode.geoPoint?.lat ?: 0.0,
                                    lng = barcode.geoPoint?.lng ?: 0.0
                                ),
                                codeImage = it,
                                userCreated = 2
                            )
                            pushCode(entity)
                        },
                        onFail = {
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
                    )
                }
            }
        }
    }
}