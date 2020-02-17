package com.example.user.l_1_1;

public class DateTime {
    private int year;
    private String month;
    private int day;
    private int hour;
    private int minute;
    private int second;
    private String antePostMeridian;

    public DateTime(String timeStamp) {
        String[] values = timeStamp.split(",");
        int year = Integer.parseInt(values[3]);
        String month = values[1];
        int day = Integer.parseInt(values[2]);
        int hour = Integer.parseInt(values[4]);
        int minute = Integer.parseInt(values[5]);
        int seconds = Integer.parseInt(values[6]);
        String amPm = values[7];
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.second = seconds;
        this.antePostMeridian = amPm;
    }

    public String getAntePostMeridian() {
        return antePostMeridian;
    }

    public int getYear() {
        return year;
    }

    public String getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public int getSecond() {
        return second;
    }

    @Override
    public String toString() {
        return getDay() + "/" + getMonth() + "/" + getYear() + " " + getHour() + ":" + getMinute() + ":" + getSecond() + " " + getAntePostMeridian();
    }
}