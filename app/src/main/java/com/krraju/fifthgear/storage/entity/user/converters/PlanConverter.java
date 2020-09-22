package com.krraju.fifthgear.storage.entity.user.converters;

import androidx.room.TypeConverter;

import com.krraju.fifthgear.storage.entity.user.enums.Plan;

public class PlanConverter {

    @TypeConverter
    public static String fromPlan(Plan plan){
        switch (plan){
            case ANNUAL: return "Annual";
            case MONTHLY: return "Monthly";
            case QUARTERLY: return "Quarterly";
            case HALF_YEARLY: return "Half Yearly";
        }
        return null;
    }

    @TypeConverter
    public static Plan stringToPlan(String plan){
        switch (plan){
            case "Annual" : return Plan.ANNUAL;
            case "Monthly" : return Plan.MONTHLY;
            case "Quarterly" : return Plan.QUARTERLY;
            case "Half Yearly" : return Plan.HALF_YEARLY;
        }
        return null;
    }
}
