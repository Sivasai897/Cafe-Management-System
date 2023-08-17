package com.in.cafe.constants;

public class CafeConstants {

    public static final String Wrong_Message = "SOMETHING WENT WRONG";

    public static final String INVALID_DATA = "INVALID DATA";

    public static final String UNAUTHORIZED_ACCESS = "NOT ACCESSIBLE";

    private CafeConstants(){
        throw new IllegalStateException("Utility Class");
    }
}
