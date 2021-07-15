package com.krraju.fifthgear.storage.entity.expenditure;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.krraju.fifthgear.storage.entity.user.converters.DateConverter;

import java.time.LocalDate;

import lombok.Data;

@Entity(tableName = "expenditure")
@Data
public class Expenditure {

    // == Fields ==
    @PrimaryKey(autoGenerate = true)
    private int serialNumber;

    @TypeConverters(DateConverter.class)
    private LocalDate date;

    private String shortDescription;

    private String detailedDescription;

    private float amount;
}
