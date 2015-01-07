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

    public boolean hasCredentials(Context aContext) {
        return getUserAccount(aContext) != null;
    }

    public void saveLastCheckDate(Context aContext, Date aDate) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(aContext);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(PREFS_LAST_CHECK_DATE_KEY, aDate.getTime());
        editor.commit();
    }

    public Date getLastCheckDate(Context aContext) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(aContext);
        return new Date(prefs.getLong(PREFS_LAST_CHECK_DATE_KEY, 0));
    }

    public void removeLastCheckDate(Context aContext) {
        removeFromPrefs(aContext, PREFS_LAST_CHECK_DATE_KEY);
    }

    public UserAccount getUserAccount(Context aContext) {
        String login = getFromPrefs(aContext, PREFS_LOGIN_USERNAME_KEY, null);
        String password = getFromPrefs(aContext, PREFS_LOGIN_PASSWORD_KEY, null);
        if (login != null && password != null) {
            return new UserAccount(login, password);
        }
        return null;
    }

    public void saveUserAccount(Context aContext, UserAccount aUserAccount) {
        saveToPrefs(aContext, PREFS_LOGIN_USERNAME_KEY, aUserAccount.getEmailAddress());
        saveToPrefs(aContext, PREFS_LOGIN_PASSWORD_KEY, aUserAccount.getPassword());
    }

    public void removeUserAccount(Context aContext) {
        removeFromPrefs(aContext, PREFS_LOGIN_PASSWORD_KEY);
        removeFromPrefs(aContext, PREFS_LOGIN_USERNAME_KEY);
    }

    public void saveLastSeenEmail(Context aContext, Email aEmail) {
        saveToPrefs(aContext, EMAIL_SUBJECT, aEmail.getSubject());
        saveToPrefs(aContext, EMAIL_SENDER_EMAIL, aEmail.getSenderEmail());
        saveToPrefs(aContext, EMAIL_SENT_DATE, aEmail.getSentDate().getTime());
        saveToPrefs(aContext, EMAIL_CONTENT, aEmail.getContent());
    }

    public Email getLastSeenEmail(Context aContext) {
        Email email = new Email();
        email.setSenderEmail(getFromPrefs(aContext, EMAIL_SENDER_EMAIL, ""));
        email.setSubject(getFromPrefs(aContext, EMAIL_SUBJECT, ""));
        email.setSentDate(new Date(getFromPrefs(aContext, EMAIL_SENT_DATE, 0)));
        email.setContent(getFromPrefs(aContext, EMAIL_CONTENT, ""));
        return email;
    }

    private void saveToPrefs(Context aContext, String aKey, String aValue) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(aContext);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putString(aKey, aValue);
        editor.commit();
    }

    private void saveToPrefs(Context aContext, String aKey, long aValue) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(aContext);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(aKey, aValue);
        editor.commit();
    }

    private String getFromPrefs(Context aContext, String aKey, String aDefaultValue) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(aContext);
        return sharedPrefs.getString(aKey, aDefaultValue);
    }


    private long getFromPrefs(Context aContext, String aKey, long aDefaultValue) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(aContext);
        return sharedPrefs.getLong(aKey, aDefaultValue);
    }

    private void removeFromPrefs(Context aContext, String aKey) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(aContext);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.remove(aKey);
        editor.commit();
    }
}

