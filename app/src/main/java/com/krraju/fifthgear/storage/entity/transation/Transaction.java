package com.krraju.fifthgear.storage.entity.transation;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.krraju.fifthgear.storage.entity.user.converters.DateConverter;

import java.time.LocalDate;

import lombok.Data;
import lombok.ToString;

@Entity
@Data
@ToString
public class Transaction {
    @PrimaryKey(autoGenerate = true)
    private int serialNumber;

    @TypeConverters(DateConverter.class)
    private LocalDate date;

    private float amount;

    @ColumnInfo(name = "user_id")
    private int userId;

    // == Constructor ==
    public Transaction(LocalDate date, float amount, int userId) {
        this.date = date;
        this.amount = amount;
        this.userId = userId;
    }
}
