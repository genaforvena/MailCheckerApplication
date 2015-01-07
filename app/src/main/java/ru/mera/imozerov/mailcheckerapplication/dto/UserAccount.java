package ru.mera.imozerov.mailcheckerapplication.dto;

import android.text.TextUtils;

import ru.mera.imozerov.mailcheckerapplication.exceptions.EmptyCredentialsException;

/**
 * Created by imozerov on 23.12.2014.
 */
public class UserAccount {
    private final String mEmailAddress;
    private final String mPassword;

    public UserAccount(String aEmailAddress, String aPassword) throws EmptyCredentialsException {
        if (TextUtils.isEmpty(aEmailAddress) || TextUtils.isEmpty(aPassword)) {
            throw new EmptyCredentialsException(aEmailAddress, aPassword);
        }
        this.mEmailAddress = aEmailAddress;
        this.mPassword = aPassword;
    }

    public String getEmailAddress() {
        return mEmailAddress;
    }

    public String getPassword() {
        return mPassword;
    }

    @Override
    public String toString() {
        return getEmailAddress();
    }
}
