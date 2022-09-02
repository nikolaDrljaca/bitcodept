package com.drbrosdev.qrscannerfromlib.repo

import com.drbrosdev.qrscannerfromlib.database.CodeDatabase
import com.drbrosdev.qrscannerfromlib.database.QRCodeEntity

class CodeRepository(db: CodeDatabase) {
    private val dao = db.codeDao()

    val listOfCodes = dao.fetchNonUserCreatedCodes()
    val listOfUserCodes = dao.fetchUserCreatedCodes()

    suspend fun fetchCodeById(id: Int) = dao.getCode(id)

    suspend fun deleteCode(code: QRCodeEntity) = dao.deleteCode(code)
    suspend fun insertCode(code: QRCodeEntity) = dao.insertCode(code)
    suspend fun deleteAllCodes() = dao.deleteAllCodes()
}