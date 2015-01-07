package ru.mera.imozerov.mailcheckerapplication.sharedPreferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.Date;

import ru.mera.imozerov.mailcheckerapplication.dto.Email;
import ru.mera.imozerov.mailcheckerapplication.dto.UserAccount;
import ru.mera.imozerov.mailcheckerapplication.exceptions.EmptyCredentialsException;

/**
 * Created by imozerov on 24.12.2014.
 */
public class SharedPreferencesHelper {
    private static final String TAG = SharedPreferencesHelper.class.getName();

    private static final String PREFS_LOGIN_USERNAME_KEY = "__USERNAME__";
    private static final String PREFS_LOGIN_PASSWORD_KEY = "__PASSWORD__";
    private static final String PREFS_NEXT_CHECK_DATE_KEY = "__NEXT_CHECK_DATE__";
    private static final String EMAIL_SUBJECT = "__EMAIL_SUBJECT__";
    private static final String EMAIL_SENDER_EMAIL = "__EMAIL_SENDER_EMAIL__";
    private static final String EMAIL_SENT_DATE = "__EMAIL_SENT_DATE__";
    private static final String EMAIL_CONTENT = "__EMAIL_CONTENT__";

    public boolean hasCredentials(Context aContext) {
        return getUserAccount(aContext) != null;
    }

    public void saveNextCheckDate(Context aContext, Date aDate) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(aContext);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(PREFS_NEXT_CHECK_DATE_KEY, aDate.getTime());
        editor.commit();
    }

    public Date getNextCheckDate(Context aContext) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(aContext);
        return new Date(prefs.getLong(PREFS_NEXT_CHECK_DATE_KEY, 0));
    }

    public void removeLastCheckDate(Context aContext) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(aContext);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.remove(PREFS_NEXT_CHECK_DATE_KEY);
        editor.commit();
    }

    public UserAccount getUserAccount(Context aContext) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(aContext);
        String login = prefs.getString(PREFS_LOGIN_USERNAME_KEY, null);
        String password = prefs.getString(PREFS_LOGIN_PASSWORD_KEY, null);
        try {
            return new UserAccount(login, password);
        } catch (EmptyCredentialsException e) {
            Log.w(TAG, "User from prefs has empty credentials.", e);
            return null;
        }
    }

    public void saveUserAccount(Context aContext, UserAccount aUserAccount) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(aContext);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PREFS_LOGIN_USERNAME_KEY, aUserAccount.getEmailAddress());
        editor.putString(PREFS_LOGIN_PASSWORD_KEY, aUserAccount.getPassword());
        editor.commit();
    }

    public void removeUserAccount(Context aContext) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(aContext);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.remove(PREFS_LOGIN_PASSWORD_KEY);
        editor.remove(PREFS_LOGIN_USERNAME_KEY);
        editor.commit();
    }

    public void saveLastSeenEmail(Context aContext, Email aEmail) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(aContext);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putString(EMAIL_SUBJECT, aEmail.getSubject());
        editor.putString(EMAIL_SENDER_EMAIL, aEmail.getSenderEmail());
        editor.putLong(EMAIL_SENT_DATE, aEmail.getSentDate().getTime());
        editor.putString(EMAIL_CONTENT, aEmail.getContent());
        editor.commit();
    }

    public Email getLastSeenEmail(Context aContext) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(aContext);
        Email email = new Email();
        email.setSenderEmail(sharedPrefs.getString(EMAIL_SENDER_EMAIL, ""));
        email.setSubject(sharedPrefs.getString(EMAIL_SUBJECT, ""));
        email.setSentDate(new Date(sharedPrefs.getLong(EMAIL_SENT_DATE, 0)));
        email.setContent(sharedPrefs.getString(EMAIL_CONTENT, ""));
        return email;
    }
}

