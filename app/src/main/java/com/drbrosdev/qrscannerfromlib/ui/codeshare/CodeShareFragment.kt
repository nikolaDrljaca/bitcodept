package com.drbrosdev.qrscannerfromlib.ui.codeshare

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Build.VERSION_CODES.P
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.navArgs
import coil.load
import com.drbrosdev.qrscannerfromlib.R
import com.drbrosdev.qrscannerfromlib.anims.fadeTo
import com.drbrosdev.qrscannerfromlib.databinding.FragmentCodeShareBinding
import com.drbrosdev.qrscannerfromlib.util.QRGenUtils
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class CodeShareFragment : BottomSheetDialogFragment() {
    private val args: CodeShareFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.ThemeOverlay_Demo_BottomSheetDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_code_share, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentCodeShareBinding.bind(view)

        binding.apply {
            cardCodeImage.setCardBackgroundColor(args.colorInt)
            chipGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
                when (checkedId) {
                    R.id.button_clean -> if (isChecked) {
                        layoutWatermark.fadeTo(false)
                    }

                    R.id.button_watermark -> if (isChecked) {
                        layoutWatermark.fadeTo(true)
                    }
                    else -> {}
                }
            }
            //load code into ImageView
            val bmp = QRGenUtils.createCodeBitmap(
                codeContent = args.rawValue,
                colorInt = args.colorInt,
                width = 450,
                height = 450
            )
            imageViewCode.load(bmp)

            buttonShare.setOnClickListener {
                val qrCodeBitmap = Bitmap.createBitmap(
                    cardCodeImage.width,
                    cardCodeImage.height,
                    Bitmap.Config.ARGB_8888
                )
                val canvas = Canvas(qrCodeBitmap)
                cardCodeImage.draw(canvas)

                val uri = createCodeImageUri(qrCodeBitmap)
                val imShareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_STREAM, uri)
                    data = uri
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    type = "image/png"
                }
                val intent = Intent.createChooser(imShareIntent, null)
                startActivity(intent)
            }
        }

        binding.imageViewAppIcon.apply {
            val shape = shapeAppearanceModel.toBuilder()
                .setAllCornerSizes(14f)
                .build()
            shapeAppearanceModel = shape
        }
    }

    private fun createCodeImageUri(bitmap: Bitmap): Uri {
        val filename = "bc_code_im_${UUID.randomUUID().toString()}.png"
        val imagePath = File(requireContext().filesDir, "images")
        if (!imagePath.exists()) imagePath.mkdirs()
        val newFile = File(imagePath, filename)
        FileOutputStream(newFile).use {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
        }
        return FileProvider.getUriForFile(
            requireContext(),
            "com.drbrosdev.qrscannerfromlib.fileprovider",
            newFile
        )
    }
}