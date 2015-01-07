package ru.mera.imozerov.mailcheckerapplication.exceptions;

import android.text.TextUtils;

/**
 * Created by imozerov on 07.01.2015.
 */
public class EmptyCredentialsException extends Throwable {
    private boolean mIsEmailEmpty = false;
    private boolean mIsPasswordEmpty = false;

    public EmptyCredentialsException(String aEmailAddress, String aPassword) {
        super();
        if (TextUtils.isEmpty(aEmailAddress)) {
            mIsEmailEmpty = true;
        }
        if (TextUtils.isEmpty(aPassword)) {
            mIsPasswordEmpty = true;
        }
    }

    @Override
    public String getMessage() {
        return "Is email empty: " + mIsEmailEmpty + "; Is password empty: " + mIsPasswordEmpty;
    }
}
