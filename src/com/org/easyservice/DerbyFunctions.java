package com.org.easyservice;

import java.util.Date;

public class DerbyFunctions 
{
    public static String toFormatDate(java.sql.Date dt)
    {
        String[] date = dt.toString().split("-");
        return date[2] + "." + date[1] + "." + date[0];
    }
}
