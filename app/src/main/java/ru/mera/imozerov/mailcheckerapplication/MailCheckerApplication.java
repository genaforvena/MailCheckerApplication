package ru.mera.imozerov.mailcheckerapplication;

import android.app.Application;

import ru.mera.imozerov.mailcheckerapplication.mail.MailHelper;

/**
 * Created by imozerov on 25.12.2014.
 */
public class MailCheckerApplication extends Application {
    private MailHelper mMailHelper;

    public synchronized MailHelper getMailHelper() {
        return mMailHelper;
    }

    public synchronized void setMailHelper(MailHelper mMailHelper) {
        this.mMailHelper = mMailHelper;
    }
}
