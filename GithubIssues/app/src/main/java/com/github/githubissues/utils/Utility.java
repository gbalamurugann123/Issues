package com.github.githubissues.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Ponns on 4/2/2017.
 */

public class Utility {

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static String dateFormat(String date)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        Date myDate = null;
        try {
            myDate = dateFormat.parse(date);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        //SimpleDateFormat timeFormat = new SimpleDateFormat("MM-dd-yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("MMM dd,yyyy hh:mm a");
        String finalDate = timeFormat.format(myDate);
        return finalDate;
    }
}
