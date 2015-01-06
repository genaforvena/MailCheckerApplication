package ru.mera.imozerov.mailcheckerapplication.sharedPreferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Date;

import ru.mera.imozerov.mailcheckerapplication.dto.Email;
import ru.mera.imozerov.mailcheckerapplication.dto.UserAccount;

/**
 * Created by imozerov on 24.12.2014.
 */
public class SharedPreferencesHelper {
    private static final String TAG = SharedPreferencesHelper.class.getName();

    private static final String PREFS_LOGIN_USERNAME_KEY = "__USERNAME__";
    private static final String PREFS_LOGIN_PASSWORD_KEY = "__PASSWORD__";
    private static final String PREFS_LAST_CHECK_DATE_KEY = "__LAST_CHECK_DATE__";
    private static final String EMAIL_SUBJECT = "__EMAIL_SUBJECT__";
    private static final String EMAIL_SENDER_EMAIL = "__EMAIL_SENDER_EMAIL__";
    private static final String EMAIL_SENT_DATE = "__EMAIL_SENT_DATE__";
    private static final String EMAIL_CONTENT = "__EMAIL_CONTENT__";

    public boolean isLoggedIn(Context context) {
        return getUserAccount(context) != null;
    }

    public void saveLastCheckDate(Context context, Date date) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(PREFS_LAST_CHECK_DATE_KEY, date.getTime());
        editor.commit();
    }

    public Date getLastCheckDate(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return new Date(prefs.getLong(PREFS_LAST_CHECK_DATE_KEY, 0));
    }

    public void removeLastCheckDate(Context context) {
        removeFromPrefs(context, PREFS_LAST_CHECK_DATE_KEY);
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

    public void saveLastSeenEmail(Context context, Email email) {
        saveToPrefs(context, EMAIL_SUBJECT, email.getSubject());
        saveToPrefs(context, EMAIL_SENDER_EMAIL, email.getSenderEmail());
        saveToPrefs(context, EMAIL_SENT_DATE, email.getSentDate().getTime());
        saveToPrefs(context, EMAIL_CONTENT, email.getContent());
    }

    public Email getLastSeenEmail(Context context) {
        Email email = new Email();
        email.setSenderEmail(getFromPrefs(context, EMAIL_SENDER_EMAIL, ""));
        email.setSubject(getFromPrefs(context, EMAIL_SUBJECT, ""));
        email.setSentDate(new Date(getFromPrefs(context, EMAIL_SENT_DATE, 0)));
        email.setContent(getFromPrefs(context, EMAIL_CONTENT, ""));
        return email;
    }

    private void saveToPrefs(Context context, String key, String value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.commit();
    }

    private void saveToPrefs(Context context, String key, long value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    private String getFromPrefs(Context context, String key, String defaultValue) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPrefs.getString(key, defaultValue);
    }


    private long getFromPrefs(Context context, String key, long defaultValue) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPrefs.getLong(key, defaultValue);
    }

    private void removeFromPrefs(Context context, String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.remove(key);
        editor.commit();
    }
}

