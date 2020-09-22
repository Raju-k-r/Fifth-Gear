package com.krraju.fifthgear.storage.database;

import android.content.Context;
import android.os.Environment;

import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.krraju.fifthgear.storage.dao.TransactionDao;
import com.krraju.fifthgear.storage.dao.UserDao;
import com.krraju.fifthgear.storage.entity.transation.Transaction;
import com.krraju.fifthgear.storage.entity.user.User;
import com.krraju.fifthgear.storage.entity.user.enums.Plan;
import com.krraju.fifthgear.storage.entity.user.enums.Status;

import java.time.LocalDate;


@androidx.room.Database(entities = {User.class, Transaction.class}, version = 1)
public abstract class Database extends RoomDatabase {

    // == Constants ==
    private static final String BASE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/FifthGear";

    // == Singleton object ==
    private static Database database = null;

    // == Data Access Object ==
    public abstract UserDao userDao();
    public abstract TransactionDao transactionDao();

    // == Method to Create the singleton instance of the class ==
    public static Database getInstance(Context context){
        if(database == null){
            // == Creating the Database ==
            database = Room.databaseBuilder(context,Database.class, "fgf_database.db").build();
        }
        return database;
    }

    // == Some Public Methods ==
    public synchronized int addTransaction(Transaction transaction){

        // == Adding the Transaction ==
        transactionDao().addTransaction(transaction);

        // == Getting the User ==
        User user = userDao().getUser(transaction.getUserId());

        // == Getting the User Due Amount ==
        float dueAmount = user.getDueAmount();

        // == updating the dueAmount ==
        dueAmount -= transaction.getAmount();

        // == updating the due amount of the user ==
        return userDao().updateDueAmount(dueAmount, transaction.getUserId());
    }
    
    public synchronized int topUpUser(int userId, Plan plan, float fees){
        User user = userDao().getUser(userId);
        float dueAmount = user.getDueAmount();
        dueAmount += fees;
        LocalDate dueDate = null;

        if(user.getDueDate() == null || user.getDueDate().isBefore(LocalDate.now())){
            switch (plan) {
                case HALF_YEARLY:
                    dueDate = LocalDate.now().plusMonths(6).minusDays(1);
                    break;
                case QUARTERLY:
                    dueDate = LocalDate.now().plusMonths(3).minusDays(1);
                    break;
                case MONTHLY:
                    dueDate = LocalDate.now().plusMonths(1).minusDays(1);
                    break;
                case ANNUAL:
                    dueDate = LocalDate.now().plusYears(1).minusDays(1);
                    break;
            }
        }else{
            switch (plan) {
                case HALF_YEARLY:
                    dueDate = user.getDueDate().plusMonths(6);
                    break;
                case QUARTERLY:
                    dueDate = user.getDueDate().plusMonths(3);
                    break;
                case MONTHLY:
                    dueDate = user.getDueDate().plusMonths(1);
                    break;
                case ANNUAL:
                    dueDate = user.getDueDate().plusYears(1);
                    break;
            }
        }


        return userDao().topUpUser(userId,dueAmount,fees,dueDate, Status.ACTIVE,plan);
    }

}
