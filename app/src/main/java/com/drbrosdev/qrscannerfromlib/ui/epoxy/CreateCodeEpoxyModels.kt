package com.drbrosdev.qrscannerfromlib.ui.epoxy

import androidx.annotation.DrawableRes
import androidx.core.widget.addTextChangedListener
import coil.load
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.drbrosdev.qrscannerfromlib.R
import com.drbrosdev.qrscannerfromlib.databinding.ModelCreateCodeItemBinding
import com.drbrosdev.qrscannerfromlib.databinding.ModelCreateContactBinding
import com.drbrosdev.qrscannerfromlib.databinding.ModelCreateEmailBinding
import com.drbrosdev.qrscannerfromlib.databinding.ModelCreatePhoneBinding
import com.drbrosdev.qrscannerfromlib.databinding.ModelCreatePlainBinding
import com.drbrosdev.qrscannerfromlib.databinding.ModelCreateSmsBinding
import com.drbrosdev.qrscannerfromlib.databinding.ModelCreateUrlBinding
import com.drbrosdev.qrscannerfromlib.databinding.ModelCreateWifiBinding

@EpoxyModelClass
abstract class CreateSmsEpoxyModel :
    ViewBindingKotlinModel<ModelCreateSmsBinding>(R.layout.model_create_sms) {

    @EpoxyAttribute
    var phoneNumber: String = ""

    @EpoxyAttribute
    lateinit var onPhoneNumberChanged: (String) -> Unit

    @EpoxyAttribute
    var message: String = ""

    @EpoxyAttribute
    lateinit var onMessageChanged: (String) -> Unit

    override fun ModelCreateSmsBinding.bind() {
        editTextMessage.setText(message)
        editTextPhoneNumber.setText(phoneNumber)

        editTextMessage.addTextChangedListener { onMessageChanged(it.toString()) }
        editTextPhoneNumber.addTextChangedListener { onPhoneNumberChanged(it.toString()) }
    }
}

@EpoxyModelClass
abstract class CreateEmailEpoxyModel :
    ViewBindingKotlinModel<ModelCreateEmailBinding>(R.layout.model_create_email) {

    @EpoxyAttribute
    var email: String = ""

    @EpoxyAttribute
    lateinit var onEmailChanged: (String) -> Unit

    @EpoxyAttribute
    var subject: String = ""

    @EpoxyAttribute
    lateinit var onSubjectChanged: (String) -> Unit

    @EpoxyAttribute
    var body: String = ""

    @EpoxyAttribute
    lateinit var onBodyChanged: (String) -> Unit

    override fun ModelCreateEmailBinding.bind() {
        editTextEmail.setText(email)
        editTextSubject.setText(subject)
        editTextSubject.setText(body)

        editTextEmail.addTextChangedListener { onEmailChanged(it.toString()) }
        editTextSubject.addTextChangedListener { onSubjectChanged(it.toString()) }
        editTextBody.addTextChangedListener { onBodyChanged(it.toString()) }
    }
}

@EpoxyModelClass
abstract class CreatePlainEpoxyModel :
    ViewBindingKotlinModel<ModelCreatePlainBinding>(R.layout.model_create_plain) {

    @EpoxyAttribute
    var text: String = ""

    @EpoxyAttribute
    lateinit var onTextChanged: (String) -> Unit

    override fun ModelCreatePlainBinding.bind() {
        editText.setText(text)
        editText.addTextChangedListener { onTextChanged(it.toString()) }
    }
}


@EpoxyModelClass
abstract class CreatePhoneEpoxyModel :
    ViewBindingKotlinModel<ModelCreatePhoneBinding>(R.layout.model_create_phone) {

    @EpoxyAttribute
    var phoneNumber: String = ""

    @EpoxyAttribute
    lateinit var onPhoneNumberChanged: (String) -> Unit

    override fun ModelCreatePhoneBinding.bind() {
        editTextPhoneNumber.setText(phoneNumber)
        editTextPhoneNumber.addTextChangedListener { onPhoneNumberChanged(it.toString()) }
    }
}

@EpoxyModelClass
abstract class CreateWifiEpoxyModel :
    ViewBindingKotlinModel<ModelCreateWifiBinding>(R.layout.model_create_wifi) {

    @EpoxyAttribute
    var ssid: String = ""

    @EpoxyAttribute
    lateinit var onSsidChanged: (String) -> Unit

    @EpoxyAttribute
    var password: String = ""

    @EpoxyAttribute
    lateinit var onPasswordChanged: (String) -> Unit

    override fun ModelCreateWifiBinding.bind() {
        editTextSsid.setText(ssid)
        editTextPassword.setText(password)

        editTextPassword.addTextChangedListener { onPasswordChanged(it.toString()) }
        editTextSsid.addTextChangedListener { onSsidChanged(it.toString()) }

    }
}

@EpoxyModelClass
abstract class CreateContactEpoxyModel :
    ViewBindingKotlinModel<ModelCreateContactBinding>(R.layout.model_create_contact) {

    @EpoxyAttribute
    var email: String = ""

    @EpoxyAttribute
    lateinit var onEmailChanged: (String) -> Unit

    @EpoxyAttribute
    var phone: String = ""

    @EpoxyAttribute
    lateinit var onPhoneChanged: (String) -> Unit

    @EpoxyAttribute
    var name: String = ""

    @EpoxyAttribute
    lateinit var onNameChanged: (String) -> Unit

    override fun ModelCreateContactBinding.bind() {
        editTextEmail.setText(email)
        editTextName.setText(name)
        editTextPhone.setText(phone)

        editTextEmail.addTextChangedListener { onEmailChanged(it.toString()) }
        editTextName.addTextChangedListener { onNameChanged(it.toString()) }
        editTextPhone.addTextChangedListener { onPhoneChanged(it.toString()) }
    }
}

@EpoxyModelClass
abstract class CreateUrlEpoxyModel :
    ViewBindingKotlinModel<ModelCreateUrlBinding>(R.layout.model_create_url) {

    @EpoxyAttribute
    var url: String = ""

    @EpoxyAttribute
    lateinit var onUrlChanged: (String) -> Unit

    override fun ModelCreateUrlBinding.bind() {
        editText.setText(url)
        editText.addTextChangedListener { onUrlChanged(it.toString()) }
    }
}

@EpoxyModelClass
abstract class CreateCodeTypeEpoxyModel :
    ViewBindingKotlinModel<ModelCreateCodeItemBinding>(R.layout.model_create_code_item) {

    @EpoxyAttribute
    var colorInt = 0

    @EpoxyAttribute
    var codeTypeName: String = ""

    @EpoxyAttribute
    lateinit var onClick: () -> Unit

    @EpoxyAttribute
    @DrawableRes
    var imageRes = 0

    override fun ModelCreateCodeItemBinding.bind() {
        card.setOnClickListener { onClick() }
        card.setCardBackgroundColor(colorInt)
        tvPrice.text = codeTypeName
        imageViewCodeType.load(imageRes)
    }
}
