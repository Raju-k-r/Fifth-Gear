package com.krraju.fifthgear.storage.entity.user.enums;

public enum Status {
    ACTIVE,
    INACTIVE;

    // == The Function used for converting the String to Status ==
    public Status getStatusFromString(String status){

        // == Checking the status String ==

        switch (status){
            // == Checking for ACTIVE string ==
            case "ACTIVE":
                return ACTIVE;

            // == Checking for INACTIVE string ==
            case "INACTIVE":
                return INACTIVE;
        }
        // == if no result matched then returning null ==
        return null;
    }
}
