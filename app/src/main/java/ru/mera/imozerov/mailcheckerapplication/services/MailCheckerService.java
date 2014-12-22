package ru.mera.imozerov.mailcheckerapplication.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import ru.mera.imozerov.mailcheckerapplication.MailCheckerApi;
import ru.mera.imozerov.mailcheckerapplication.NewMailListener;

public class MailCheckerService extends Service {
    private static final String TAG = MailCheckerService.class.getName();

    private final Object mLock = new Object();
    private List<NewMailListener> listeners = new ArrayList<NewMailListener>();
    private MailCheckerApi.Stub mMailCheckerApi = new MailCheckerApiImplementation();
    private TimerTask mUpdateTask = new UpdateTask();
    private Timer mTimer;

    public MailCheckerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        if (MailCheckerService.class.getName().equals(intent.getAction())) {
            Log.d(TAG, "Bound by intent " + intent);
            return mMailCheckerApi;
        } else {
            return null;
        }
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

    public Timer getTimer() {
        return mTimer;
    }

    class MailCheckerApiImplementation extends MailCheckerApi.Stub {

        @Override
        public void login(String username, String password) throws RemoteException {

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
