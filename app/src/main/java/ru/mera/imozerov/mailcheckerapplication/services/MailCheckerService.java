package ru.mera.imozerov.mailcheckerapplication.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import ru.mera.imozerov.mailcheckerapplication.BuildConfig;
import ru.mera.imozerov.mailcheckerapplication.MailCheckerApi;
import ru.mera.imozerov.mailcheckerapplication.NewMailListener;
import ru.mera.imozerov.mailcheckerapplication.R;
import ru.mera.imozerov.mailcheckerapplication.activities.EmailListActivity;
import ru.mera.imozerov.mailcheckerapplication.database.EmailsDataSource;
import ru.mera.imozerov.mailcheckerapplication.dto.Email;
import ru.mera.imozerov.mailcheckerapplication.dto.UserAccount;
import ru.mera.imozerov.mailcheckerapplication.mail.MailHelper;
import ru.mera.imozerov.mailcheckerapplication.sharedPreferences.SharedPreferencesHelper;

public class MailCheckerService extends Service {
    public static final String LOGIN = MailCheckerService.class.getName() + "attemptLogin";
    public static final String PASSWORD = MailCheckerService.class.getName() + "password";
    private static final String TAG = MailCheckerService.class.getName();
    protected MailHelper mMailHelper;
    private List<NewMailListener> listeners = new ArrayList<>();
    private MailCheckerApi.Stub mMailCheckerApi = new MailCheckerApiImplementation();
    private TimerTask mUpdateTask = new UpdateTask();
    private Timer mTimer;
    private SharedPreferencesHelper mSharedPreferencesHelper = new SharedPreferencesHelper();
    private EmailsDataSource mEmailsDataSource = new EmailsDataSource(this);
    private NotificationManager mNotificationManager;

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
            synchronized (this) {
                return MailCheckerService.this.isLoggedIn();
            }
        }

        @Override
        public void login(String login, String password) throws RemoteException {
            synchronized (this) {
                Log.i(TAG, "Logging in to " + login);
                if (login != null && !login.isEmpty() && password != null && !password.isEmpty()) {
                    UserAccount userAccount = new UserAccount(login, password);
                    new SharedPreferencesHelper().saveUserAccount(MailCheckerService.this, userAccount);
                    scheduleTask();
                } else {
                    Log.e(TAG, "Check you're passing all values! Login: " + login + "; Password: " + password);
                }
            }
        }

        @Override
        public List<Email> getAllEmails() throws RemoteException {
            synchronized (this) {
                Log.i(TAG, "Getting all emails");
                try {
                    mEmailsDataSource.open();
                    return mEmailsDataSource.getAllEmails();
                } catch (SQLException e) {
                    e.printStackTrace();
                    return null;
                } finally {
                    mEmailsDataSource.close();
                }
            }
        }

        @Override
        public void addNewMailListener(NewMailListener listener) throws RemoteException {
            synchronized (this) {
                listeners.add(listener);
            }
        }

        @Override
        public void removeNewMailListener(NewMailListener listener) throws RemoteException {
            synchronized (this) {
                listeners.remove(listener);
            }
        }
    }

    class UpdateTask extends TimerTask {
        @Override
        public void run() {
            Log.i(TAG, "Timer task doing work");
            if (mMailHelper != null) {
                Date lastCheckDate = new Date();
                List<Email> emails = mMailHelper.getEmailsFromInbox(mSharedPreferencesHelper.getLastCheckDate(MailCheckerService.this));
                if (BuildConfig.DEBUG) {
                    if (emails != null) {
                        Log.d(TAG, "Got " + emails.size() + " emails");
                    } else {
                        Log.d(TAG, "Attempted to get emails. But there are no");
                    }
                }
                if (emails != null) {
                    try {
                        mEmailsDataSource.open();
                        for (Email email : emails) {
                            mEmailsDataSource.saveEmail(email);
                        }
                        notifyListeners();
                        sendNotification(emails);
                        mSharedPreferencesHelper.saveLastCheckDate(MailCheckerService.this, lastCheckDate);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    } finally {
                        mEmailsDataSource.close();
                    }
                }
            }
        }
    }

    private void sendNotification(List<Email> emails) {
        Log.i(TAG, "Sending notification");
        if (emails != null) {
            String notificationText;
            String notificationTitle;
            if (emails.size() == 1) {
                Email email = emails.get(0);
                notificationTitle = "You've got new email!";
                notificationText = "Email from " + email.getSenderEmail();
            } else {
                notificationTitle = "You've got some new emails!";
                notificationText = emails.size() + " new emails";
            }
            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.ic_launcher)
                            .setContentTitle(notificationTitle)
                            .setAutoCancel(true)
                            .setContentText(notificationText);
            int NOTIFICATION_ID = 12345;

            Intent targetIntent = new Intent(this, EmailListActivity.class);
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0, targetIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(contentIntent);
            if (mNotificationManager == null) {
                mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            }
            mNotificationManager.notify(NOTIFICATION_ID, builder.build());
        }
    }

    private void notifyListeners() {
        for (NewMailListener listener : listeners) {
            try {
                listener.handleNewMail();
            } catch (RemoteException e) {
                Log.w(TAG, "Failed to notify listener " + listener, e);
            }
        }
    }
}
