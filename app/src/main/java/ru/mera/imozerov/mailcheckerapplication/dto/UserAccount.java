package ru.mera.imozerov.mailcheckerapplication.dto;

/**
 * Created by imozerov on 23.12.2014.
 */
public class UserAccount {
    private final String mEmailAddress;
    private final String mPassword;

    public UserAccount(String aEmailAddress, String aPassword) {
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
