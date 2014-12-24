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
import ru.mera.imozerov.mailcheckerapplication.mail.MailHelper;

public class MailCheckerService extends Service {
    private static final String TAG = MailCheckerService.class.getName();

    public static final String LOGIN = MailCheckerService.class.getName() + "login";
    public static final String PASSWORD = MailCheckerService.class.getName() + "password";

    private final Object mLock = new Object();
    private List<NewMailListener> listeners = new ArrayList<NewMailListener>();
    private MailCheckerApi.Stub mMailCheckerApi = new MailCheckerApiImplementation();
    private MailHelper mMailHelper;
    private boolean mIsLoggedIn = false;
    private TimerTask mUpdateTask = new UpdateTask();
    private Timer mTimer;

    public MailCheckerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        if (MailCheckerService.class.getName().equals(intent.getAction())) {
            String login = intent.getStringExtra(LOGIN);
            String password = intent.getStringExtra(PASSWORD);
            boolean isLoginAttempt = login != null && password != null;
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "onBind; isLoggedIn = " + isLoggedIn() + "; isLoginAttempt = " + isLoginAttempt);
            }
            if (isLoggedIn() || isLoginAttempt) {
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "Bound by intent " + intent);
                }
                return mMailCheckerApi;
            }
        }
        return null;

    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Creating service instance");

        mTimer = new Timer("MailCheckerTimer");
        mTimer.schedule(mUpdateTask, 1000L, 60 * 1000L);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "Destroying service instance");
    }

    public boolean isLoggedIn() {
        return mIsLoggedIn;
    }

    public void setLoggedIn(boolean mIsLoggedIn) {
        this.mIsLoggedIn = mIsLoggedIn;
    }

    public Timer getTimer() {
        return mTimer;
    }

    class MailCheckerApiImplementation extends MailCheckerApi.Stub {

        @Override
        public void login(String username, String password) throws RemoteException {
            synchronized (this) {
                mMailHelper = new MailHelper(new UserAccount(username, password));
                setLoggedIn(true);
            }
        }

        @Override
        public boolean isLoggedIn() throws RemoteException {
            return isLoggedIn();
        }

        @Override
        public void addListener(NewMailListener listener) throws RemoteException {
            synchronized (listeners) {
                listeners.add(listener);
            }
        }

        @Override
        public void removeListener(NewMailListener listener) throws RemoteException {
            synchronized (listeners) {
                listeners.remove(listener);
            }
        }
    };

    class UpdateTask extends TimerTask {
        @Override
        public void run() {
            Log.i(TAG, "Timer task doing work");
            synchronized (listeners) {
                for (NewMailListener listener : listeners) {
                    try {
                        listener.handleNewMail();
                    } catch (RemoteException e) {
                        Log.w(TAG, "Failed to notify listener " + listener, e);
                    }
                }
            }
        }
    };
}
