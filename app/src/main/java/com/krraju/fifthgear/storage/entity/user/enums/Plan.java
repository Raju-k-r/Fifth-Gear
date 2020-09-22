package com.krraju.fifthgear.storage.entity.user.enums;

public enum Plan {
    MONTHLY,
    QUARTERLY,
    HALF_YEARLY,
    ANNUAL;

    // == The Function used for converting the String to Plan ==
    public static Plan fromString(String plan) {

        // == Checking the plan String ==

        switch (plan) {
            // == Checking for SELF EMPLOYED string ==
            case "MONTHLY":
                return MONTHLY;

            // == Checking for EMPLOYED string ==
            case "QUARTERLY":
                return QUARTERLY;

            // == Checking for SERVICE string ==
            case "HALF YEARLY":
                return HALF_YEARLY;

            // == Checking for STUDENT string ==
            case "ANNUAL":
                return ANNUAL;
        }

        // == if no result matched then returning null ==
        return null;
    }
}
