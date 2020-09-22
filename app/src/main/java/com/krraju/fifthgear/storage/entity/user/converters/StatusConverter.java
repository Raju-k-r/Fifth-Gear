package com.krraju.fifthgear.storage.entity.user.converters;

import androidx.room.TypeConverter;

import com.krraju.fifthgear.storage.entity.user.enums.Status;

import static com.krraju.fifthgear.storage.entity.user.enums.Status.ACTIVE;
import static com.krraju.fifthgear.storage.entity.user.enums.Status.INACTIVE;

public class StatusConverter {


    @TypeConverter
    public static String fromStatus(Status status){
        switch (status){
            case INACTIVE: return "INACTIVE";
            case ACTIVE: return "ACTIVE";
        }
        return null;
    }

    @TypeConverter
    public static Status stringToStatus(String status) {

        switch (status) {
            case "INACTIVE":
                return INACTIVE;
            case "ACTIVE":
                return ACTIVE;
        }
        return null;
    }
}
