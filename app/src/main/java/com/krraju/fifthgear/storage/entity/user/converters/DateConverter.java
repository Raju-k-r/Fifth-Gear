package com.krraju.fifthgear.storage.entity.user.converters;

import android.annotation.SuppressLint;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.room.TypeConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RequiresApi(api = Build.VERSION_CODES.O)
public class DateConverter {

    @SuppressLint("SimpleDateFormat")
    private static final DateTimeFormatter format = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @TypeConverter
    public static String getDate(LocalDate dueDate){
        if(dueDate == null){
            return "00-00-0000";
        }
        return format.format(dueDate);
    }

    @TypeConverter
    public static LocalDate stringToData(String dueDate){
        if(dueDate.equals("00-00-0000")){
            return null;
        }
        Log.d("TAG", "stringToData: " + dueDate);
        return LocalDate.parse(dueDate,DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }


}
