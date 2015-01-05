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
import ru.mera.imozerov.mailcheckerapplication.database.EmailsDataSource;
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
    private SharedPreferencesHelper mSharedPreferencesHelper = new SharedPreferencesHelper();
    protected MailHelper mMailHelper;
    private EmailsDataSource mEmailsDataSource = new EmailsDataSource(this);

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "binding service");
        return mMailCheckerApi;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Starting service");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (isLoggedIn()) {
            scheduleTask();
        }
    }

    private void scheduleTask() {
        UserAccount userAccount = mSharedPreferencesHelper.getUserAccount(this);
        setMailHelper(new MailHelper(userAccount));
        mTimer = new Timer("MailCheckerTimer");
        mTimer.schedule(mUpdateTask, 1000L, 60 * 1000L);
    }

    private boolean isLoggedIn() {
        return mSharedPreferencesHelper.isLoggedIn(this);
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
        public void login(String login, String password) throws RemoteException {
            Log.i(TAG, "Logging in to " + login);
            if (login != null && !login.isEmpty() && password != null && !password.isEmpty()) {
                UserAccount userAccount = new UserAccount(login, password);
                new SharedPreferencesHelper().saveUserAccount(MailCheckerService.this, userAccount);
                scheduleTask();
            } else {
                Log.e(TAG, "Check you're passing all values! Login: " + login + "; Password: " + password);
            }
        }

        @Override
        public List<Email> getAllEmails() throws RemoteException {
            return mEmailsDataSource.getAllEmails();
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
                if (BuildConfig.DEBUG) {
                    if (emails != null) {
                        Log.d(TAG, "Got " + emails.size() + " emails");
                    } else {
                        Log.d(TAG, "Attempted to get emails. But there are no");
                    }
                }
                if (emails != null) {
                    for (Email email : emails) {
                        mEmailsDataSource.saveEmail(email);
                    }
                }
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
