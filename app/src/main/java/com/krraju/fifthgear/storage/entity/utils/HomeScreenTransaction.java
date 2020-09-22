package com.krraju.fifthgear.storage.entity.utils;

import android.content.Context;

import com.krraju.fifthgear.storage.database.Database;
import com.krraju.fifthgear.storage.entity.transation.Transaction;

import java.time.LocalDate;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class HomeScreenTransaction {
    private int user_id;
    private String name;
    private float amount;
    private LocalDate date;

    public HomeScreenTransaction(Transaction transaction, Context context) {
        this.user_id = transaction.getUserId();
        this.name = Database.getInstance(context).userDao().getUserName(user_id);
        this.amount = transaction.getAmount();
        this.date = transaction.getDate();
    }
}
