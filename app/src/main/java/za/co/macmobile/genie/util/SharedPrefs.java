package za.co.macmobile.genie.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import za.co.macmobile.genie.R;

public class SharedPrefs {
    private final String backgroundColour = "background_colour";
    private static  final String secondaryColour = "secondary_colour";
    private static final String avatar = "avatar";

    static SharedPreferences getPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void saveAvatar(Context context, String avatr) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putString(avatar, avatr);
        editor.apply();
    }

    public static String retrieveAvatar(Context context) {
        return getPreferences(context).getString(avatar, "av_geniecolour");
    }

    public static void saveSecColour(Context context, String colour) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putString(secondaryColour, colour);
        editor.apply();
    }
    public static String retrieveSecColour(Context context) {
        return getPreferences(context).getString(secondaryColour, "#3F51B5");
    }

}
