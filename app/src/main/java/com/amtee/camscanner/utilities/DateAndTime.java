package com.amtee.camscanner.utilities;

import java.util.Calendar;

/**
 * Created by DEVEN SINGH on 2/21/2015.
 */
public class DateAndTime {


    public String DateNTime() {
        Calendar calendar = Calendar.getInstance();
        String month = "" + (calendar.get(Calendar.MONTH) + 1);
        String day = "" + calendar.get(Calendar.DAY_OF_MONTH);
        String hour = "" + calendar.get(Calendar.HOUR_OF_DAY);
        String minute = "" + calendar.get(Calendar.MINUTE);
        String second = "" + calendar.get(Calendar.SECOND);
        if ((calendar.get(Calendar.MONTH) + 1) < 10) {
            month = "0" + (calendar.get(Calendar.MONTH) + 1);
        }
        if (calendar.get(Calendar.DAY_OF_MONTH) < 10) {
            day = "0" + calendar.get(Calendar.DAY_OF_MONTH);
        }
        if (calendar.get(Calendar.HOUR_OF_DAY) < 10) {
            hour = "0" + calendar.get(Calendar.HOUR_OF_DAY);
        }
        if (calendar.get(Calendar.MINUTE) < 10) {
            minute = "0" + calendar.get(Calendar.MINUTE);
        }
        if (calendar.get(Calendar.SECOND) < 10) {
            second = "0" + calendar.get(Calendar.SECOND);
        }
        String aDateTym = calendar.get(Calendar.YEAR) + "/" + month + "/"+ day + "  "+ hour + ":"+ minute + ":"+ second;
        return aDateTym;
    }


}
