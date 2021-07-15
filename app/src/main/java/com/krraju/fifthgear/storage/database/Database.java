package com.krraju.fifthgear.storage.database;

import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteStatement;

import com.krraju.fifthgear.storage.dao.ExpenditureDao;
import com.krraju.fifthgear.storage.dao.TransactionDao;
import com.krraju.fifthgear.storage.dao.UserDao;
import com.krraju.fifthgear.storage.entity.expenditure.Expenditure;
import com.krraju.fifthgear.storage.entity.transation.Transaction;
import com.krraju.fifthgear.storage.entity.user.User;
import com.krraju.fifthgear.storage.entity.user.converters.ImageConverter;
import com.krraju.fifthgear.storage.entity.user.enums.Plan;
import com.krraju.fifthgear.storage.entity.user.enums.Status;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDate;


@androidx.room.Database(entities = {User.class, Transaction.class, Expenditure.class}, version = 3)
public abstract class Database extends RoomDatabase {

    // == Constants ==
    private static final String BASE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/FifthGear";
    public static final String TAG = "Database";
    public static final String DATABASE_NAME = "fgf_database.db";
    public static final String BACKUP_FILE_NAME = "backup.db";

    // == Singleton object ==
    private static Database database = null;

    // == Data Access Object ==
    public abstract UserDao userDao();

    public abstract TransactionDao transactionDao();

    public abstract ExpenditureDao expenditureDao();

    // == Method to Create the singleton instance of the class ==
    public static Database getInstance(Context context) {
        if (database == null) {
            // == Creating the Database ==
            database = Room.databaseBuilder(context, Database.class, DATABASE_NAME)
                    .addMigrations(migration_1_2)
                    .addMigrations(migration_2_3)
                    .build();
        }
        return database;
    }

    // == Some Public Methods ==
    public synchronized int addTransaction(Transaction transaction) {

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

    public synchronized int topUpUser(int userId, Plan plan, float fees) {
        User user = userDao().getUser(userId);
        float dueAmount = user.getDueAmount();
        dueAmount += fees;
        LocalDate dueDate = null;

        if (user.getDueDate() == null || user.getDueDate().isBefore(LocalDate.now())) {
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
        } else {
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

        return userDao().topUpUser(userId, dueAmount, fees, dueDate, Status.ACTIVE, plan);
    }

    public static void backUp(Context context) {

        database.close();
        database = getInstance(context);
        String databasePath = database.getOpenHelper().getWritableDatabase().getPath();
        File database = new File(databasePath);

        File backUpFile = new File(BASE_PATH, BACKUP_FILE_NAME);

        boolean isFileDeleted = backUpFile.delete();
        Log.d(TAG, "backUp: isFileDeleted->" + isFileDeleted);

        try {
            if (!backUpFile.exists()) {
                boolean result = backUpFile.createNewFile();
                Log.d(TAG, "backUp: New File Created ->" + result);
            }

            try (InputStream inputStream = new FileInputStream(database);
                 OutputStream outputStream = new FileOutputStream(backUpFile)) {

                byte[] bytes = new byte[1024];
                while (inputStream.read(bytes) > 0) {
                    outputStream.write(bytes, 0, bytes.length);
                }
            }

        } catch (IOException e) {
            Toast.makeText(context, "Something Went wrong please try after sometime..", Toast.LENGTH_SHORT).show();
        }
    }

    public static void restore(Context context) {

        // == Creating a backup file ==
        File backUpFile = new File(BASE_PATH, BACKUP_FILE_NAME);

        // == Checking fro backup file ==
        if (!backUpFile.exists()) {
            Toast.makeText(context, "No Backup File found", Toast.LENGTH_SHORT).show();
            return;
        }

        // == Getting the path of the database ==
        String databasePath = context.getDataDir().getPath() + "/databases/fgf_database.db";
        String databasePathSHM = context.getDataDir().getPath() + "/databases/fgf_database.db-shm";
        String databasePathWAL = context.getDataDir().getPath() + "/databases/fgf_database.db-wal";

        // == Creating the database file ==
        File database = new File(databasePath);
        File databaseSHM = new File(databasePathSHM);
        File databaseWAL = new File(databasePathWAL);

        // == Deleting the Previous Database ==
        Log.d(TAG, "restore: database deletion->" + database.delete());
        Log.d(TAG, "restore: database-shm deletion->" + databaseSHM.delete());
        Log.d(TAG, "restore: database-wal deletion->" + databaseWAL.delete());

        Database.database = Room.databaseBuilder(context, Database.class, DATABASE_NAME)
                .createFromFile(backUpFile)
                .build();
    }

    // == Creating new Migration From Version 1 to Version 2 ==
    private static final Migration migration_1_2 = new Migration(1,2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("DROP TABLE IF EXISTS User_new");
            database.execSQL("CREATE TABLE 'User_new' ('user_id' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 'first_name' TEXT, 'last_name' TEXT, 'age' INTEGER NOT NULL, 'gender' TEXT, 'height' REAL NOT NULL, 'weight' REAL NOT NULL, 'address' TEXT, 'mobile_number' TEXT, 'email' TEXT, 'plan' TEXT, 'occupation' TEXT, 'fees' REAL NOT NULL, 'health_issue' TEXT, 'image' BLOB, 'joiningDate' TEXT, 'due_date' TEXT, 'due_amount' REAL NOT NULL, 'status' TEXT)");

            Cursor cursor = database.query("SELECT * FROM user");
            boolean result = cursor.moveToFirst();

            String query = "INSERT INTO User_new VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            SupportSQLiteStatement supportSQLiteStatement = database.compileStatement(query);

            while (result) {
                supportSQLiteStatement.clearBindings();
                supportSQLiteStatement.bindLong(1,cursor.getInt(0));
                supportSQLiteStatement.bindString(2,cursor.getString(1));
                supportSQLiteStatement.bindString(3,cursor.getString(2));
                supportSQLiteStatement.bindLong(4,cursor.getInt(3));
                supportSQLiteStatement.bindString(5,cursor.getString(4));
                supportSQLiteStatement.bindDouble(6,cursor.getFloat(5));
                supportSQLiteStatement.bindDouble(7,cursor.getFloat(6));
                supportSQLiteStatement.bindString(8,cursor.getString(7));
                supportSQLiteStatement.bindString(9,cursor.getString(8));
                supportSQLiteStatement.bindString(10,cursor.getString(9));
                supportSQLiteStatement.bindString(11,cursor.getString(10));
                supportSQLiteStatement.bindString(12,cursor.getString(11));
                supportSQLiteStatement.bindDouble(13,cursor.getFloat(12));
                supportSQLiteStatement.bindString(14,cursor.getString(13));
                supportSQLiteStatement.bindBlob(15,ImageConverter.fromByteArray(BitmapFactory.decodeFile(cursor.getString(14))));
                supportSQLiteStatement.bindString(16,cursor.getString(15));
                supportSQLiteStatement.bindString(17,cursor.getString(16));
                supportSQLiteStatement.bindDouble(18,cursor.getFloat(17));
                supportSQLiteStatement.bindString(19,cursor.getString(18));
                supportSQLiteStatement.execute();
                result = cursor.moveToNext();
            }
            database.execSQL("DROP TABLE User");
            database.execSQL("ALTER TABLE User_new RENAME TO User");
        }
    };

    // == Creating new Migration From Version 2 to Version 3 ==
    private static final Migration migration_2_3 = new Migration(2,3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE 'expenditure' ('serialNumber' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 'date' TEXT, 'detailedDescription' TEXT, 'shortDescription' TEXT, 'amount' REAL NOT NULL)");
        }
    };
}
