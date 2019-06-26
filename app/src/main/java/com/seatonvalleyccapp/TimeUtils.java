package com.seatonvalleyccapp;

import java.text.DateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by julius on 29/03/2018.
 */

public class TimeUtils {
    public static String getUKDate(long timeMills){
        long time = timeMills;
        DateFormat df = DateFormat.getInstance();
        df.setTimeZone(TimeZone.getTimeZone("Europe/London"));
        return df.format(new Date(time));
    }
}
