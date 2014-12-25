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
import ru.mera.imozerov.mailcheckerapplication.dto.UserAccount;
import ru.mera.imozerov.mailcheckerapplication.sharedPreferences.SharedPreferencesHelper;

public class MailCheckerService extends Service {
    public static final String LOGIN = MailCheckerService.class.getName() + "attemptLogin";
    public static final String PASSWORD = MailCheckerService.class.getName() + "password";
    private static final String TAG = MailCheckerService.class.getName();

    private List<NewMailListener> listeners = new ArrayList<NewMailListener>();
    private MailCheckerApi.Stub mMailCheckerApi = new MailCheckerApiImplementation();
    private TimerTask mUpdateTask = new UpdateTask();
    private Timer mTimer;
    private UserAccount mUserAccount;
    private SharedPreferencesHelper mSharedPreferencesHelper = new SharedPreferencesHelper();

    public MailCheckerService() {
    }

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
            mTimer = new Timer("MailCheckerTimer");
            mTimer.schedule(mUpdateTask, 1000L, 60 * 1000L);
        } else {
            Log.i(TAG, "Stopping service as no user logged in");
            stopSelf();
        }
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "Destroying service instance");
    }

    private void attemptLogin(Intent intent) {
        String login = intent.getStringExtra(LOGIN);
        String password = intent.getStringExtra(PASSWORD);
        mUserAccount = new UserAccount(login, password);
        login();
    }

    private void login() {
        if (mUserAccount != null) {
            mSharedPreferencesHelper.saveUserAccount(this, mUserAccount);
        }
    }

    private boolean isLoggedIn() {
        return mSharedPreferencesHelper.getUserAccount(this) != null;
    }

    class MailCheckerApiImplementation extends MailCheckerApi.Stub {

        @Override
        public void login(String login, String password) throws RemoteException {
            if (login != null && !"".equals(login.trim()) && password != null && !"".equals(password.trim())) {
                mUserAccount = new UserAccount(login, password);
                MailCheckerService.this.login();
            }
        }

        @Override
        public boolean isLoggedIn() throws RemoteException {
            return isLoggedIn();
        }

        @Override
        public void addListener(NewMailListener listener) throws RemoteException {
            listeners.add(listener);
        }

        @Override
        public void removeListener(NewMailListener listener) throws RemoteException {
            listeners.remove(listener);
        }
    }

    class UpdateTask extends TimerTask {
        @Override
        public void run() {
            Log.i(TAG, "Timer task doing work");
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
