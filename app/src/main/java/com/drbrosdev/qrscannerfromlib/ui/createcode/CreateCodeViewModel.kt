package com.drbrosdev.qrscannerfromlib.ui.createcode

import androidx.lifecycle.ViewModel

class CreateCodeViewModel: ViewModel() {

    data class CodeItem(
        val colorInt: Int = 0,
        val name: String = "",
        val type: CodeType,
    )

    fun createCodeItems(colorMap: Map<String, Int>): List<CodeItem> {
        return listOf(
            CodeItem(colorMap["email"] ?: 0, "Email", CodeType.EMAIL),
            CodeItem(colorMap["url"] ?: 0, "URL", CodeType.URL),
            CodeItem(colorMap["sms"] ?: 0, "SMS", CodeType.SMS),
            CodeItem(colorMap["text"] ?: 0, "Plain", CodeType.PLAIN),
            CodeItem(colorMap["contact"] ?: 0, "Contact", CodeType.CONTACT),
            CodeItem(colorMap["phone"] ?: 0, "Phone\nNumber", CodeType.PHONE),
            CodeItem(colorMap["wifi"] ?: 0, "Wifi", CodeType.WIFI),
        )
    }
}