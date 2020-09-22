package com.krraju.fifthgear.storage.entity.user.enums;

public enum Occupation {
    SELF_EMPLOYED,
    EMPLOYED,
    SERVICE,
    STUDENT;

    // == The Function used for converting the String to Occupation ==
    public static Occupation fromString(String occupation) {

        // == Checking the occupation String ==

        switch (occupation) {
            // == Checking for SELF EMPLOYED string ==
            case "SELF EMPLOYED":
                return SELF_EMPLOYED;

            // == Checking for EMPLOYED string ==
            case "EMPLOYED":
                return EMPLOYED;

            // == Checking for SERVICE string ==
            case "SERVICE":
                return SERVICE;

            // == Checking for STUDENT string ==
            case "STUDENT":
                return STUDENT;
        }

        // == if no result matched then returning null ==
        return null;
    }
}
