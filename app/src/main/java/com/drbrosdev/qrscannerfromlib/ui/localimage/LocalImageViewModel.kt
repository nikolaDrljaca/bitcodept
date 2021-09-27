package com.drbrosdev.qrscannerfromlib.ui.localimage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drbrosdev.qrscannerfromlib.database.QRCodeEntity
import com.drbrosdev.qrscannerfromlib.model.QRCodeModel
import com.google.mlkit.vision.barcode.Barcode
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import logcat.logcat

class LocalImageViewModel : ViewModel() {
    private val localListFlow = MutableStateFlow<List<QRCodeEntity?>>(emptyList())
    private val errorMessage = MutableStateFlow("")

    val state = combine(localListFlow, errorMessage) { codes, error ->
        LocalImageViewState(codes, error)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), LocalImageViewState())


    fun submitCodes(codes: List<Barcode>) = viewModelScope.launch {
        val entities = codes.map { mapBarcodeToEntity(it) }
        localListFlow.emit(entities)
    }

    fun submitError(message: String) = viewModelScope.launch {
        errorMessage.emit(message)
    }

    private fun mapBarcodeToEntity(barcode: Barcode): QRCodeEntity? {
        return when (barcode.valueType) {
            Barcode.TYPE_CONTACT_INFO -> {
                QRCodeEntity(
                    data = QRCodeModel.ContactInfoModel(
                        rawValue = barcode.rawValue ?: "",
                        name = barcode.contactInfo?.name?.formattedName!!,
                        email = barcode.contactInfo?.emails?.first()?.address ?: "",
                        phone = barcode.contactInfo?.phones?.first()?.number ?: ""
                    )
                )
            }
            Barcode.TYPE_EMAIL -> {
                QRCodeEntity(
                    data = QRCodeModel.EmailModel(
                        rawValue = barcode.rawValue ?: "",
                        address = barcode.email?.address ?: "",
                        body = barcode.email?.body ?: "",
                        subject = barcode.email?.subject ?: "",
                    )
                )
            }
            Barcode.TYPE_PHONE -> {
                QRCodeEntity(
                    data = QRCodeModel.PhoneModel(
                        rawValue = barcode.rawValue ?: "",
                        number = barcode.phone?.number ?: ""
                    )
                )
            }
            Barcode.TYPE_SMS -> {
                QRCodeEntity(
                    data = QRCodeModel.SmsModel(
                        rawValue = barcode.rawValue ?: "",
                        message = barcode.sms?.message ?: "",
                        phoneNumber = barcode.sms?.phoneNumber ?: "",
                    )
                )
            }
            Barcode.TYPE_TEXT -> {
                QRCodeEntity(
                    data = QRCodeModel.PlainModel(
                        rawValue = barcode.rawValue ?: ""
                    ),
                    codeImage = barcode.rawValue?.toByteArray()
                )
            }
            Barcode.TYPE_URL -> {
                QRCodeEntity(
                    data = QRCodeModel.UrlModel(
                        rawValue = barcode.rawValue ?: "",
                        link = barcode.url?.url ?: "",
                        title = barcode.url?.title ?: ""
                    )
                )
            }
            Barcode.TYPE_GEO -> {
                QRCodeEntity(
                    data = QRCodeModel.GeoPointModel(
                        rawValue = barcode.rawValue ?: "",
                        lat = barcode.geoPoint?.lat ?: 0.0,
                        lng = barcode.geoPoint?.lng ?: 0.0
                    )
                )
            }
            else -> null
        }
    }
}