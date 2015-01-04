package ru.mera.imozerov.mailcheckerapplication.sharedPreferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import ru.mera.imozerov.mailcheckerapplication.dto.UserAccount;

/**
 * Created by imozerov on 24.12.2014.
 */
public class SharedPreferencesHelper {
    public static final String PREFS_LOGIN_USERNAME_KEY = "__USERNAME__";
    public static final String PREFS_LOGIN_PASSWORD_KEY = "__PASSWORD__";
    private static final String TAG = SharedPreferencesHelper.class.getName();

    public boolean isLoggedIn(Context context) {
        return getUserAccount(context) != null;
    }

    public UserAccount getUserAccount(Context context) {
        String login = getFromPrefs(context, PREFS_LOGIN_USERNAME_KEY, null);
        String password = getFromPrefs(context, PREFS_LOGIN_PASSWORD_KEY, null);
        if (login != null && password != null) {
            return new UserAccount(login, password);
        }
        return null;
    }

    public void saveUserAccount(Context context, UserAccount account) {
        saveToPrefs(context, PREFS_LOGIN_USERNAME_KEY, account.getEmailAddress());
        saveToPrefs(context, PREFS_LOGIN_PASSWORD_KEY, account.getPassword());
    }

    public void removeUserAccount(Context context) {
        removeFromPrefs(context, PREFS_LOGIN_PASSWORD_KEY);
        removeFromPrefs(context, PREFS_LOGIN_USERNAME_KEY);
    }

    private void saveToPrefs(Context context, String key, String value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.commit();
    }

    private String getFromPrefs(Context context, String key, String defaultValue) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPrefs.getString(key, defaultValue);
    }

    private void removeFromPrefs(Context context, String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.remove(key);
        editor.commit();
    }
}

