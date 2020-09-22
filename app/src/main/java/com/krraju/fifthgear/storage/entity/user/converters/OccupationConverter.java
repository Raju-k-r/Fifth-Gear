package com.krraju.fifthgear.storage.entity.user.converters;

import androidx.room.TypeConverter;

import com.krraju.fifthgear.storage.entity.user.enums.Occupation;

public class OccupationConverter {

    @TypeConverter
    public static String fromOccupation(Occupation occupation){
        switch (occupation){
            case SERVICE: return "Service";

            case STUDENT: return "Student";

            case EMPLOYED: return "Employed";

            case SELF_EMPLOYED: return "Self Employed";
        }
        return null;
    }

    @TypeConverter
    public static Occupation stringToOccupation(String occupation){
        switch (occupation){
            case "Service": return Occupation.SERVICE;

            case "Student": return Occupation.STUDENT;

            case "Employed": return Occupation.EMPLOYED;

            case "Self Employed": return Occupation.SELF_EMPLOYED;
        }
        return null;
    }
}
