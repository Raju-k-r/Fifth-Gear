package com.krraju.fifthgear.storage.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.krraju.fifthgear.storage.entity.expenditure.Expenditure;

import java.util.List;

@Dao
public interface ExpenditureDao {

    @Insert
    void addNewExpenditure(Expenditure expenditure);

    @Query("SELECT * FROM expenditure order by serialNumber desc")
    List<Expenditure> getAllExpenditure();

    @Query("SELECT * FROM 'expenditure' WHERE serialNumber = :serialNumber")
    Expenditure getExpenditure(int serialNumber);

}
