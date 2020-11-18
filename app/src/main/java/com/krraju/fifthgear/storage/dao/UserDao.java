package com.krraju.fifthgear.storage.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.TypeConverters;

import com.krraju.fifthgear.storage.entity.user.User;
import com.krraju.fifthgear.storage.entity.user.converters.DateConverter;
import com.krraju.fifthgear.storage.entity.user.converters.PlanConverter;
import com.krraju.fifthgear.storage.entity.user.converters.StatusConverter;
import com.krraju.fifthgear.storage.entity.user.enums.Plan;
import com.krraju.fifthgear.storage.entity.user.enums.Status;

import java.time.LocalDate;
import java.util.List;

@Dao
public interface UserDao {

    @Insert
    void addNewUser(User user);

    @Query("SELECT * FROM user ORDER BY first_name")
    List<User> getAllUser();

    @Delete
    int deleteUser(User user);

    @Query("SELECT * FROM user WHERE user_id = :id")
    User getUser(int id);

    @Query("SELECT * FROM user WHERE first_name = :firstName AND last_name = :lastName")
    int isUserPresent(String firstName, String lastName);

    @Query("UPDATE user SET due_date = :dueDate, due_amount= :dueAmount WHERE user_id = :userId")
    int updateDueDateAndDueAmount(String dueDate, float dueAmount, int userId);

    @Query("UPDATE user SET due_amount = :dueAmount WHERE user_id = :userId")
    int updateDueAmount(float dueAmount, int userId);

    @TypeConverters({PlanConverter.class, DateConverter.class, StatusConverter.class})
    @Query("UPDATE user SET due_amount = :dueAmount, due_date= :dueDate, status=:status, fees=:fees, 'plan'=:plan WHERE user_id= :userId")
    int topUpUser(int userId, float dueAmount, float fees, LocalDate dueDate, Status status, Plan plan);

    @Query("SELECT first_name FROM user WHERE user_id = :user_id")
    String getUserName(int user_id);

    @TypeConverters({DateConverter.class, StatusConverter.class})
    @Query("UPDATE user SET due_date = :dueDate, status = :status WHERE user_id = :userId")
    void updateDueDateAndStatus(LocalDate dueDate, Status status, int userId);

    @Query("SELECT * FROM 'user' WHERE due_amount != 0.0 ORDER BY due_amount DESC")
    List<User> getUsersHavingDueAmount();

    @TypeConverters(DateConverter.class)
    @Query("SELECT * FROM 'user' ORDER BY due_date")
    List<User> getUserOnDueDate();

    @TypeConverters({StatusConverter.class, DateConverter.class})
    @Query("UPDATE user SET first_name = :firstName, last_name = :lastName, mobile_number= :phoneNumber, gender=:gender, status=:status, due_date=:date,due_amount =:dueAmount WHERE user_id = :userId")
    int updateUserDetails(String firstName, String lastName, String phoneNumber,String gender, Status status, LocalDate date, float dueAmount,  int userId);

    @Query("UPDATE user SET due_amount = 0.0 WHERE user_id = :userId")
    void clearAllDueAmount(int userId);
}
