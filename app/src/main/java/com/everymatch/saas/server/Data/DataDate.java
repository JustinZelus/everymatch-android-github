package com.everymatch.saas.server.Data;

import java.io.Serializable;

/**
 * Created by Dacid on 29/06/2015.
 */
public class DataDate implements Serializable {
    public int day;
    public int month;
    public int year;
    public int hour;
    public int minute;
    public int second;
    public DataTime time_zone;
    public String status;

    public boolean isSameDay(DataDate other) {

        if (other == null){
            return false;
        }

        return (this.year == other.year) &&
                (this.month == other.month) &&
                (this.day == other.day);
    }

    public String getHourString() {
        String answer = String.format("%02d", hour) + ":" +
                String.format("%02d", minute);
        return answer;
    }

    public String getYearString() {
        String answer = day + "/" +
                String.format("%02d", month) + "/" +
                String.format("%02d", year);
        return answer;
    }

    public boolean hasEndTime(){
        return (hour != 23) && (minute != 59) && (second != 59);
    }
}
