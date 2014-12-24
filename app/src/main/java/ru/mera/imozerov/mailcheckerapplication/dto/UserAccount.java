package ru.mera.imozerov.mailcheckerapplication.dto;

import android.support.annotation.NonNull;

/**
 * Created by imozerov on 23.12.2014.
 */
public class UserAccount {
    private final String mLogin;
    private final String mPassword;

    public UserAccount(@NonNull String mLogin, @NonNull String mPassword) {
        this.mLogin = mLogin;
        this.mPassword = mPassword;
    }

    public String getLogin() {
        return mLogin;
    }

    public String getPassword() {
        return mPassword;
    }
}
