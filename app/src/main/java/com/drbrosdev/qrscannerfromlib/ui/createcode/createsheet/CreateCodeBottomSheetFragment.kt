package com.drbrosdev.qrscannerfromlib.ui.createcode.createsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.drbrosdev.qrscannerfromlib.R
import com.drbrosdev.qrscannerfromlib.anims.fadeTo
import com.drbrosdev.qrscannerfromlib.databinding.FragmentCreateCodeBottomSheetBinding
import com.drbrosdev.qrscannerfromlib.ui.createcode.CodeType
import com.drbrosdev.qrscannerfromlib.ui.createcode.CreateCodeEvents
import com.drbrosdev.qrscannerfromlib.ui.epoxy.createContact
import com.drbrosdev.qrscannerfromlib.ui.epoxy.createEmail
import com.drbrosdev.qrscannerfromlib.ui.epoxy.createPhone
import com.drbrosdev.qrscannerfromlib.ui.epoxy.createPlain
import com.drbrosdev.qrscannerfromlib.ui.epoxy.createSms
import com.drbrosdev.qrscannerfromlib.ui.epoxy.createUrl
import com.drbrosdev.qrscannerfromlib.ui.epoxy.createWifi
import com.drbrosdev.qrscannerfromlib.util.collectFlow
import com.drbrosdev.qrscannerfromlib.util.decideQrCodeImage
import com.drbrosdev.qrscannerfromlib.util.getColor
import com.drbrosdev.qrscannerfromlib.util.setDrawable
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class CreateCodeBottomSheetFragment : DialogFragment() {
    private val viewModel: CreateCodeBottomViewModel by viewModel()
    private val args: CreateCodeBottomSheetFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.CustomDialog);
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_create_code_bottom_sheet, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentCreateCodeBottomSheetBinding.bind(view)

        when (args.codeType) {
            CodeType.URL -> binding.coordinatorLayout.setBackgroundColor(getColor(R.color.candy_red))
            CodeType.SMS -> binding.coordinatorLayout.setBackgroundColor(getColor(R.color.candy_orange))
            CodeType.EMAIL -> binding.coordinatorLayout.setBackgroundColor(getColor(R.color.candy_blue))
            CodeType.PHONE -> binding.coordinatorLayout.setBackgroundColor(getColor(R.color.candy_green))
            CodeType.WIFI -> binding.coordinatorLayout.setBackgroundColor(getColor(R.color.candy_mandarin))
            CodeType.PLAIN -> binding.coordinatorLayout.setBackgroundColor(getColor(R.color.candy_teal))
            CodeType.CONTACT -> binding.coordinatorLayout.setBackgroundColor(getColor(R.color.candy_yellow))
        }

        collectFlow(viewModel.events) {
            when(it) {
                CreateCodeEvents.CodeTextIsEmpty -> {
                    binding.textViewError.fadeTo(true)
                    viewLifecycleOwner.lifecycleScope.launch {
                        delay(2000)
                        binding.textViewError.fadeTo(false)
                    }
                }
                CreateCodeEvents.CompleteAndNavigateUp -> {
                    findNavController().navigateUp()
                }
                CreateCodeEvents.ShowCodeSaved -> {
                    binding.progressBar.fadeTo(false)
                    binding.recyclerView.fadeTo(false)
                    binding.textView.fadeTo(true)
                    binding.imageViewCheck.fadeTo(true)
                }
                CreateCodeEvents.ShowLoading -> {
                    binding.progressBar.fadeTo(true)
                }
            }
        }

        binding.apply {
            textViewTitle
                .setDrawable(decideQrCodeImage(args.codeType), R.dimen.compound_drawable_size)
            recyclerView.withModels {
                when(args.codeType) {
                    CodeType.URL -> {
                        createUrl {
                            id("create_url")
                            url(viewModel.firstField)
                            onUrlChanged { viewModel.firstField = it }
                        }
                    }
                    CodeType.SMS -> {
                        createSms {
                            id("create_sms")
                            message(viewModel.secondField)
                            phoneNumber(viewModel.firstField)
                            onMessageChanged { viewModel.secondField = it }
                            onPhoneNumberChanged { viewModel.firstField = it }
                        }
                    }
                    CodeType.EMAIL -> {
                        createEmail {
                            id("create_email")
                            email(viewModel.firstField)
                            subject(viewModel.secondField)
                            body(viewModel.thirdField)
                            onEmailChanged { viewModel.firstField = it }
                            onBodyChanged { viewModel.thirdField = it }
                            onSubjectChanged { viewModel.secondField = it }
                        }
                    }
                    CodeType.PHONE -> {
                        createPhone {
                            id("create_phone")
                            phoneNumber(viewModel.firstField)
                            onPhoneNumberChanged { viewModel.firstField = it }
                        }
                    }
                    CodeType.WIFI -> {
                        createWifi {
                            id("create_wifi")
                            ssid(viewModel.firstField)
                            password(viewModel.secondField)
                            onSsidChanged { viewModel.firstField = it }
                            onPasswordChanged { viewModel.secondField = it }
                        }
                    }
                    CodeType.PLAIN -> {
                        createPlain {
                            id("create_plain")
                            text(viewModel.firstField)
                            onTextChanged { viewModel.firstField = it }
                        }
                    }
                    CodeType.CONTACT -> {
                        createContact {
                            id("create_contact")
                            email(viewModel.secondField)
                            name(viewModel.firstField)
                            phone(viewModel.thirdField)
                            onEmailChanged { viewModel.secondField = it }
                            onPhoneChanged { viewModel.thirdField = it }
                            onNameChanged { viewModel.firstField = it }
                        }
                    }
                }
            }

            buttonCreateCode.setOnClickListener {
                viewModel.saveCode(args.codeType)
            }
        }
    }
}