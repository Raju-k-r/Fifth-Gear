package com.krraju.fifthgear.storage.entity.user.converters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.room.TypeConverter;

import java.io.ByteArrayOutputStream;

public class ImageConverter {

    @TypeConverter
    public static Bitmap toBitmap(byte[] image){
        return BitmapFactory.decodeByteArray(image,0,image.length);
    }

    @TypeConverter
    public static byte[] fromByteArray(Bitmap bitmap){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100,outputStream);
        return outputStream.toByteArray();
    }
}
