package com.drbrosdev.qrscannerfromlib.ui.createcode.createsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.navArgs
import com.drbrosdev.qrscannerfromlib.R
import com.drbrosdev.qrscannerfromlib.databinding.FragmentCreateCodeBottomSheetBinding
import com.drbrosdev.qrscannerfromlib.ui.createcode.CodeType
import com.drbrosdev.qrscannerfromlib.ui.epoxy.createContact
import com.drbrosdev.qrscannerfromlib.ui.epoxy.createEmail
import com.drbrosdev.qrscannerfromlib.ui.epoxy.createPhone
import com.drbrosdev.qrscannerfromlib.ui.epoxy.createPlain
import com.drbrosdev.qrscannerfromlib.ui.epoxy.createSms
import com.drbrosdev.qrscannerfromlib.ui.epoxy.createUrl
import com.drbrosdev.qrscannerfromlib.ui.epoxy.createWifi
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class CreateCodeBottomSheetFragment : BottomSheetDialogFragment() {
    private val viewModel: CreateCodeBottomViewModel by viewModel()
    private val args: CreateCodeBottomSheetFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        when(args.codeType) {
            CodeType.URL -> setStyle(DialogFragment.STYLE_NORMAL, R.style.ThemeOverlay_BottomSheetFragment_Red)
            CodeType.SMS -> setStyle(DialogFragment.STYLE_NORMAL, R.style.ThemeOverlay_BottomSheetFragment_Orange)
            CodeType.EMAIL -> setStyle(DialogFragment.STYLE_NORMAL, R.style.ThemeOverlay_BottomSheetFragment_Blue)
            CodeType.PHONE -> setStyle(DialogFragment.STYLE_NORMAL, R.style.ThemeOverlay_BottomSheetFragment_Green)
            CodeType.WIFI -> setStyle(DialogFragment.STYLE_NORMAL, R.style.ThemeOverlay_BottomSheetFragment_Mandarin)
            CodeType.PLAIN -> setStyle(DialogFragment.STYLE_NORMAL, R.style.ThemeOverlay_BottomSheetFragment_Teal)
            CodeType.CONTACT -> setStyle(DialogFragment.STYLE_NORMAL, R.style.ThemeOverlay_BottomSheetFragment_Yellow)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_create_code_bottom_sheet, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentCreateCodeBottomSheetBinding.bind(view)

        binding.apply {
            recyclerView.withModels {
                when(args.codeType) {
                    CodeType.URL -> {
                        createUrl {
                            id("create_url")
                            url("")
                            onUrlChanged {  }
                        }
                    }
                    CodeType.SMS -> {
                        createSms {
                            id("create_sms")
                            message("")
                            phoneNumber("")
                            onMessageChanged {  }
                            onPhoneNumberChanged {  }
                        }
                    }
                    CodeType.EMAIL -> {
                        createEmail {
                            id("create_email")
                            email("")
                            subject("")
                            body("")
                            onEmailChanged { }
                            onBodyChanged { }
                            onSubjectChanged { }
                        }
                    }
                    CodeType.PHONE -> {
                        createPhone {
                            id("create_phone")
                            phoneNumber("")
                            onPhoneNumberChanged {  }
                        }
                    }
                    CodeType.WIFI -> {
                        createWifi {
                            id("create_wifi")
                            ssid("")
                            password("")
                            onSsidChanged {  }
                            onPasswordChanged {  }
                        }
                    }
                    CodeType.PLAIN -> {
                        createPlain {
                            id("create_plain")
                            text("")
                            onTextChanged {  }
                        }
                    }
                    CodeType.CONTACT -> {
                        createContact {
                            id("create_contact")
                            email("")
                            name("")
                            phone("")
                            onEmailChanged {  }
                            onPhoneChanged {  }
                            onNameChanged {  }
                        }
                    }
                }
            }

            buttonCreateCode.setOnClickListener {
            }
        }
    }
}