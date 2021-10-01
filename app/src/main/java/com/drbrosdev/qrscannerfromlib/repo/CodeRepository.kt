package com.drbrosdev.qrscannerfromlib.repo

import com.drbrosdev.qrscannerfromlib.database.CodeDatabase
import com.drbrosdev.qrscannerfromlib.database.QRCodeEntity
import com.drbrosdev.qrscannerfromlib.util.AsyncModel
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class CodeRepository(db: CodeDatabase) {
    private val dao = db.codeDao()

    fun getCodes() = flow<AsyncModel<List<QRCodeEntity>>> {
        emit(AsyncModel.Loading)
        try {
            val flow = dao.getAllCodes().map { AsyncModel.Success(it) }
            emitAll(flow)
        } catch (e: Exception) {
            emit(AsyncModel.Fail(e))
        }
    }

    fun getCodeById(codeId: Int) = flow<AsyncModel<QRCodeEntity>> {
        emit(AsyncModel.Loading)
        try {
            val code = dao.getCode(codeId)
            emit(AsyncModel.Success(code))
        } catch (e: Exception) {
            emit(AsyncModel.Fail(e))
        }
    }

    suspend fun fetchCodeById(id: Int) = dao.getCode(id)

    suspend fun deleteCode(code: QRCodeEntity) = dao.deleteCode(code)
    suspend fun insertCode(code: QRCodeEntity) = dao.insertCode(code)
    suspend fun deleteAllCodes() = dao.deleteAllCodes()
}