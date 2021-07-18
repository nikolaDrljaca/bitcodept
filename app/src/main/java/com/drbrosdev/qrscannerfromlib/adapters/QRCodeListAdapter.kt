package com.drbrosdev.qrscannerfromlib.adapters

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.drbrosdev.qrscannerfromlib.R
import com.drbrosdev.qrscannerfromlib.database.QRCodeEntity
import com.drbrosdev.qrscannerfromlib.databinding.QrCodeListItemBinding
import com.drbrosdev.qrscannerfromlib.model.QRCodeModel

class QRCodeListAdapter(
    private val onItemClicked: (QRCodeEntity) -> Unit,
    private val onDeleteClicked: (QRCodeEntity) -> Unit,
): ListAdapter<QRCodeEntity, QRCodeListAdapter.ViewHolder>(DiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = QrCodeListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, parent.context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item.data, item.codeImage ?: ByteArray(0))
    }

    inner class ViewHolder(
        private val binding: QrCodeListItemBinding,
        private val context: Context
    ): RecyclerView.ViewHolder(binding.root) {
        init {
            binding.card.setOnClickListener {
                if(absoluteAdapterPosition != RecyclerView.NO_POSITION) {
                    onItemClicked(getItem(absoluteAdapterPosition))
                }
            }
            binding.ibDelete.setOnClickListener { onDeleteClicked(getItem(absoluteAdapterPosition)) }
        }

        fun bind(model: QRCodeModel, byteArray: ByteArray) {
            val bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
            when (model) {
                is QRCodeModel.PlainModel -> {
                    binding.apply {
                        val colorInt = ContextCompat.getColor(context, R.color.candy_teal)
                        tvLabel.text = "Plain Text"
                        tvShortDesc.text = model.rawValue
                        card.setCardBackgroundColor(colorInt)
                        ivCode.load(bmp)
                        ivImage.load(R.drawable.ic_round_text_fields_24)
                    }
                }
                is QRCodeModel.SmsModel -> {
                    binding.apply {
                        val colorInt = ContextCompat.getColor(context, R.color.candy_orange)
                        tvLabel.text = "SMS"
                        tvShortDesc.text = model.phoneNumber
                        card.setCardBackgroundColor(colorInt)
                        ivImage.load(R.drawable.message_icon)
                        ivCode.load(bmp)
                    }
                }
                is QRCodeModel.UrlModel -> {
                    binding.apply {
                        val colorInt = ContextCompat.getColor(context, R.color.candy_red)
                        tvLabel.text = "Link"
                        tvShortDesc.text = model.link
                        card.setCardBackgroundColor(colorInt)
                        ivImage.load(R.drawable.link_icon)
                        ivCode.load(bmp)
                    }
                }
                is QRCodeModel.GeoPointModel -> {
                    binding.apply {
                        val colorInt = ContextCompat.getColor(context, R.color.candy_purple)
                        tvLabel.text = "Location"
                        card.setCardBackgroundColor(colorInt)
                        ivImage.load(R.drawable.globe_icon)
                        ivCode.load(bmp)
                    }
                }
                is QRCodeModel.ContactInfoModel -> {
                    binding.apply {
                        val colorInt = ContextCompat.getColor(context, R.color.candy_yellow)
                        tvLabel.text = "Contact"
                        tvShortDesc.text = model.name
                        card.setCardBackgroundColor(colorInt)
                        ivImage.load(R.drawable.contact_book_icon)
                        ivCode.load(bmp)
                    }
                }
                is QRCodeModel.EmailModel -> {
                    binding.apply {
                        val colorInt = ContextCompat.getColor(context, R.color.candy_blue)
                        tvLabel.text = "Email"
                        tvShortDesc.text = model.address
                        card.setCardBackgroundColor(colorInt)
                        ivImage.load(R.drawable.email_icon)
                        ivCode.load(bmp)
                    }
                }
                is QRCodeModel.PhoneModel -> {
                    binding.apply {
                        val colorInt = ContextCompat.getColor(context, R.color.candy_green)
                        tvLabel.text = "Phone"
                        tvShortDesc.text = model.number
                        card.setCardBackgroundColor(colorInt)
                        ivImage.load(R.drawable.phone_icon)
                        ivCode.load(bmp)
                    }
                }
            }
        }
    }

    class DiffCallback: DiffUtil.ItemCallback<QRCodeEntity>() {
        override fun areItemsTheSame(oldItem: QRCodeEntity, newItem: QRCodeEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: QRCodeEntity, newItem: QRCodeEntity): Boolean {
            return (oldItem == newItem)
        }
    }
}