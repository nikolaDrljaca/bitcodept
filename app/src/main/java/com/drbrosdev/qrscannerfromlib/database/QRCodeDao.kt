package com.drbrosdev.qrscannerfromlib.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface QRCodeDao {
    @Query("SELECT * FROM QR_CODE_TABLE ORDER BY time DESC")
    fun getAllCodes(): Flow<List<QRCodeEntity>>

    @Query("SELECT * FROM QR_CODE_TABLE WHERE id=:id")
    suspend fun getCode(id: Int): QRCodeEntity

    @Delete
    suspend fun deleteCode(code: QRCodeEntity)

    @Insert
    suspend fun insertCode(code: QRCodeEntity): Long

    @Query("DELETE FROM QR_CODE_TABLE")
    suspend fun deleteAllCodes()

    @Query("SELECT * FROM QR_CODE_TABLE WHERE userCreated=0 OR userCreated=2 ORDER BY time DESC")
    fun fetchNonUserCreatedCodes(): Flow<List<QRCodeEntity>>

    @Query("SELECT * FROM QR_CODE_TABLE WHERE userCreated=1 ORDER BY time DESC")
    fun fetchUserCreatedCodes(): Flow<List<QRCodeEntity>>

    @Update
    suspend fun updateCodes(vararg codes: QRCodeEntity)
}