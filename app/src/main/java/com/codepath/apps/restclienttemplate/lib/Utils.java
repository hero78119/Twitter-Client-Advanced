package com.codepath.apps.restclienttemplate.lib;


import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Utils {

    public static Long currentUserId;
    public static String currentUserScreenName;

    public static Date getDateFromString(String date) throws ParseException {
        // String date example: "Mon Sep 03 13:24:14 +0000 2012"
        DateFormat df = new SimpleDateFormat("EEE MMM dd kk:mm:ss Z yyyy", Locale.ENGLISH);
        Date result = df.parse(date);
        return result;
    }

    public static String transformDateToString(Date data) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(data);
    }

    public static JSONObject twitterPostPreProcessForDao(JSONObject post) throws JSONException, ParseException {
        // process date
        Date created_at = getDateFromString(post.getString("created_at"));
        post.put("created_at", transformDateToString(created_at));
        return post;
    }

    public static void setCurrentUserId(Long userId) {
        Utils.currentUserId = userId;
    }

    public static Long getCurrentUserId() {
        return Utils.currentUserId;
    }

    public static void setCurrentUserScreenName(String userScreenName) {
        Utils.currentUserScreenName = userScreenName;
    }

    public static String getCurrentUserScreenName() {
        return Utils.currentUserScreenName;
    }


    public static void logout() {
        Utils.currentUserId = null;
    }

    public static String displayTimeToTweet(Date date) {
        return new SimpleDateFormat("hh:mm a dd MMM yy").format(date);
    }

    public static String getBestTimeDiff(Date targetTime) {
        Date currentTime = new Date();
        String display;
        long day = TimeUnit.MILLISECONDS.toHours(currentTime.getTime() - targetTime.getTime());
        long hour = TimeUnit.MILLISECONDS.toHours(currentTime.getTime() - targetTime.getTime());
        long minute = TimeUnit.MILLISECONDS.toMinutes(currentTime.getTime() - targetTime.getTime());
        long seconds = TimeUnit.MILLISECONDS.toSeconds(currentTime.getTime() - targetTime.getTime());
        display = String.valueOf(day) + "d";
        if (hour < 24) {
            display = String.valueOf(hour) + "h";
        }
        if (minute < 60) {
            display = String.valueOf(minute) + "m";
        }
        if (seconds < 60) {
            display = String.valueOf(seconds) + "s";
        }
        return display;
    }

}
