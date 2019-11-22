package com.example.weatherapp;

import android.app.Activity;
import android.content.Intent;
import android.widget.RelativeLayout;

public class Utils
{
    private static int sTheme;
    public final static int THEME_DEFAULT = 0;
    public final static int THEME_LIGHT = 1;
    /**
     * Set the theme of the Activity, and restart it by creating a new Activity of the same type.
     */
    public static void changeToTheme(Activity activity, int theme)
    {
        sTheme = theme;
//        Intent i = new Intent(activity, activity.getClass());
//        activity.startActivity(i);
        activity.recreate();


    }
    /** Set the theme of the activity, according to the configuration. */
    public static void onActivityCreateSetTheme(Activity activity)
    {
        switch (sTheme)
        {
            default:
            case THEME_DEFAULT:
                activity.setTheme(R.style.CustomThemeDark);
                break;
            case THEME_LIGHT:
                activity.setTheme(R.style.CustomThemeLight);
                break;
        }
    }
    /** Set background of the activity, according to the configuration. */
    public static void setBackground(Activity activity)
    {
        RelativeLayout mainParent = activity.findViewById(R.id.mainParent);

        switch (sTheme)
        {
            default:
            case THEME_DEFAULT:
                mainParent.setBackgroundResource(R.drawable.bg_gradient);
                break;
            case THEME_LIGHT:
                mainParent.setBackgroundResource(R.drawable.bg_gradient_light);
                break;
        }
    }
}