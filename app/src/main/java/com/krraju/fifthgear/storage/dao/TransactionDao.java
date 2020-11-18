package com.krraju.fifthgear.storage.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.TypeConverters;

import com.krraju.fifthgear.storage.entity.transation.Transaction;
import com.krraju.fifthgear.storage.entity.user.converters.DateConverter;

import java.time.LocalDate;
import java.util.List;

@Dao
public interface TransactionDao {

    @Insert
    void addTransaction(Transaction transaction);

    @Query("SELECT * FROM 'transaction'")
    List<Transaction> getAllTransaction();

    @Query("SELECT * FROM 'transaction' WHERE user_id = :userId ORDER BY serialNumber DESC")
    List<Transaction> getUserTransaction(int userId);

    @TypeConverters(DateConverter.class)
    @Query("SELECT * FROM 'transaction' WHERE date = :date")
    List<Transaction> getTodaysTransaction(LocalDate date);

    @Query("SELECT * FROM 'transaction' ORDER BY serialNumber DESC LIMIT 10 ")
    List<Transaction> getLastTenTransaction();

    @TypeConverters(DateConverter.class)
    @Query("SELECT * FROM 'transaction' WHERE date BETWEEN :firstDate AND :lastDate")
    List<Transaction> getThisMonthTransaction(LocalDate firstDate, LocalDate lastDate);

    @TypeConverters(DateConverter.class)
    @Query("SELECT * FROM 'transaction' ORDER BY serialNumber DESC")
    List<Transaction> getAllTransactionInDesc();
}
