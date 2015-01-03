package ru.mera.imozerov.mailcheckerapplication.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import ru.mera.imozerov.mailcheckerapplication.BuildConfig;
import ru.mera.imozerov.mailcheckerapplication.MailCheckerApi;
import ru.mera.imozerov.mailcheckerapplication.NewMailListener;
import ru.mera.imozerov.mailcheckerapplication.dto.Email;
import ru.mera.imozerov.mailcheckerapplication.dto.UserAccount;
import ru.mera.imozerov.mailcheckerapplication.mail.MailHelper;
import ru.mera.imozerov.mailcheckerapplication.sharedPreferences.SharedPreferencesHelper;

public class MailCheckerService extends Service {
    private static final String TAG = MailCheckerService.class.getName();

    public static final String LOGIN = MailCheckerService.class.getName() + "attemptLogin";
    public static final String PASSWORD = MailCheckerService.class.getName() + "password";

    private List<NewMailListener> listeners = new ArrayList<>();
    private MailCheckerApi.Stub mMailCheckerApi = new MailCheckerApiImplementation();
    private TimerTask mUpdateTask = new UpdateTask();
    private Timer mTimer;
    private UserAccount mUserAccount;
    private SharedPreferencesHelper mSharedPreferencesHelper = new SharedPreferencesHelper();
    protected MailHelper mMailHelper;

    @Override
    public IBinder onBind(Intent intent) {
        if (MailCheckerService.class.getName().equals(intent.getAction())) {
            attemptLogin(intent);
            if (isLoggedIn()) {
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "Bound by intent " + intent);
                }
                return mMailCheckerApi;
            }
        }
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Starting service");
        if (isLoggedIn()) {
            scheduleTask();
        } else {
            Log.i(TAG, "Stopping service as user is not logged in");
            stopSelf();
        }
        return Service.START_NOT_STICKY;
    }

    private void attemptLogin(Intent intent) {
        String login = intent.getStringExtra(LOGIN);
        String password = intent.getStringExtra(PASSWORD);
        mUserAccount = new UserAccount(login, password);
        if (mUserAccount != null) {
            setMailHelper(new MailHelper(mUserAccount));
            if (mMailHelper.isAbleToLogin()) {
                mSharedPreferencesHelper.saveUserAccount(this, mUserAccount);
                scheduleTask();
            }
        }
    }

    private void scheduleTask() {
        mTimer = new Timer("MailCheckerTimer");
        mTimer.schedule(mUpdateTask, 1000L, 60 * 1000L);
    }

    private boolean isLoggedIn() {
        return mSharedPreferencesHelper.isLoggedIn(this);
    }

    TimerTask getUpdateTask() {
        return mUpdateTask;
    }

    Timer getTimer() {
        return mTimer;
    }

    void setSharedPreferencesHelper(SharedPreferencesHelper mSharedPreferencesHelper) {
        this.mSharedPreferencesHelper = mSharedPreferencesHelper;
    }

    void setMailHelper(MailHelper mMailHelper) {
        this.mMailHelper = mMailHelper;
    }

    class MailCheckerApiImplementation extends MailCheckerApi.Stub {
        @Override
        public boolean isLoggedIn() throws RemoteException {
            return MailCheckerService.this.isLoggedIn();
        }

        @Override
        public void forceRefresh() {

        }
        @Override
        public void addNewMailListener(NewMailListener listener) throws RemoteException {
            listeners.add(listener);
        }

        @Override
        public void removeNewMailListener(NewMailListener listener) throws RemoteException {
            listeners.remove(listener);
        }
    }

    class UpdateTask extends TimerTask {
        @Override
        public void run() {
            Log.i(TAG, "Timer task doing work");
            if (mMailHelper != null) {
                List<Email> emails = mMailHelper.getEmailsFromInbox();
            }
            for (NewMailListener listener : listeners) {
                try {
                    listener.handleNewMail();
                } catch (RemoteException e) {
                    Log.w(TAG, "Failed to notify listener " + listener, e);
                }
            }
        }
    }
}
