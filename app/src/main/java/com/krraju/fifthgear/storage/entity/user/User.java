package com.krraju.fifthgear.storage.entity.user;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.krraju.fifthgear.storage.entity.user.converters.DateConverter;
import com.krraju.fifthgear.storage.entity.user.converters.OccupationConverter;
import com.krraju.fifthgear.storage.entity.user.converters.PlanConverter;
import com.krraju.fifthgear.storage.entity.user.converters.StatusConverter;
import com.krraju.fifthgear.storage.entity.user.enums.Occupation;
import com.krraju.fifthgear.storage.entity.user.enums.Plan;
import com.krraju.fifthgear.storage.entity.user.enums.Status;

import java.time.LocalDate;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Data
@ToString
@EqualsAndHashCode
public class User {

    // == fields ==
    @PrimaryKey(autoGenerate = true)
    private int user_id;

    @ColumnInfo(name = "first_name")
    private String firstName;

    @ColumnInfo(name = "last_name")
    private String lastName;

    private int age;

    private String gender;

    private float height;

    private float weight;

    private String address;

    @ColumnInfo(name = "mobile_number")
    private String mobileNumber;

    private String email;

    @TypeConverters(PlanConverter.class)
    private Plan plan;

    @TypeConverters(OccupationConverter.class)
    private Occupation occupation;

    private float fees;

    @ColumnInfo(name = "health_issue")
    private String healthIssue;

    @ColumnInfo(name = "image_path")
    private String imagePath;

    @TypeConverters(DateConverter.class)
    private LocalDate joiningDate;

    @ColumnInfo(name = "due_date")
    @TypeConverters(DateConverter.class)
    private LocalDate dueDate;

    @ColumnInfo(name = "due_amount")
    private float dueAmount;

    @TypeConverters(StatusConverter.class)
    private Status status;

    // == Public Constructor ==
    public User(String firstName, String lastName, int age, String gender, float height, float weight,
                String address, String mobileNumber, String email, Plan plan, Occupation occupation,
                float fees, String healthIssue, String imagePath, Status status) {

        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.gender = gender;
        this.height = height;
        this.weight = weight;
        this.address = address;
        this.mobileNumber = mobileNumber;
        this.email = email;
        this.plan = plan;
        this.occupation = occupation;
        this.fees = fees;
        this.healthIssue = healthIssue;
        this.imagePath = imagePath;
        this.joiningDate = LocalDate.now();
        this.status = status;

        switch (status){
            case ACTIVE:
                switch (plan) {
                    case HALF_YEARLY:
                        this.dueDate = joiningDate.plusMonths(6).minusDays(1);
                        break;
                    case QUARTERLY:
                        this.dueDate = joiningDate.plusMonths(3).minusDays(1);
                        break;
                    case MONTHLY:
                        this.dueDate = joiningDate.plusMonths(1).minusDays(1);
                        break;
                    case ANNUAL:
                        this.dueDate = joiningDate.plusYears(1).minusDays(1);
                        break;
                }
                this.dueAmount = fees;
                break;
            case INACTIVE:
                this.dueDate = null;
                dueAmount = 0.0f;
        }

    }
}
