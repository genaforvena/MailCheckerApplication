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
    private List<NewMailListener> mListeners = new ArrayList<NewMailListener>();
    private MailCheckerApi.Stub mMailCheckerApi = new MailCheckerApiImplementation();
    private TimerTask mUpdateTask = new UpdateTask();
    private Timer mTimer;
    private SharedPreferencesHelper mSharedPreferencesHelper = new SharedPreferencesHelper();
    private EmailsDataSource mEmailsDataSource = new EmailsDataSource(this);
    private NotificationManager mNotificationManager;

    private Object mLock = new Object();

    @Override
    public IBinder onBind(Intent aIntent) {
        Log.i(TAG, "binding service");
        return mMailCheckerApi;
    }

    @Override
    public int onStartCommand(Intent aIntent, int aFlags, int aStartId) {
        Log.i(TAG, "Starting service");
        return super.onStartCommand(aIntent, aFlags, aStartId);
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
        return mSharedPreferencesHelper.hasCredentials(this);
    }

    Timer getTimer() {
        return mTimer;
    }

    void setSharedPreferencesHelper(SharedPreferencesHelper mSharedPreferencesHelper) {
        this.mSharedPreferencesHelper = mSharedPreferencesHelper;
    }

    void setMailHelper(MailHelper aMailHelper) {
        this.mMailHelper = aMailHelper;
    }

    class MailCheckerApiImplementation extends MailCheckerApi.Stub {

        @Override
        public boolean isLoggedIn() throws RemoteException {
            synchronized (mLock) {
                return MailCheckerService.this.isLoggedIn();
            }
        }

        @Override
        public void login(String aLogin, String aPassword) throws RemoteException {
            synchronized (mLock) {
                Log.i(TAG, "Logging in to " + aLogin);
                if (aLogin != null && !aLogin.isEmpty() && aPassword != null && !aPassword.isEmpty()) {
                    UserAccount userAccount = new UserAccount(aLogin, aPassword);
                    mSharedPreferencesHelper.saveUserAccount(MailCheckerService.this, userAccount);
                    scheduleTask();
                } else {
                    Log.e(TAG, "Check you're passing all values! Login: " + aLogin + "; Password: " + aPassword);
                }
            }
        }

        @Override
        public void logout() throws RemoteException {
            synchronized (mLock) {
                Log.i(TAG, "Logging out");
                mSharedPreferencesHelper.removeUserAccount(MailCheckerService.this);
                mSharedPreferencesHelper.removeLastCheckDate(MailCheckerService.this);
                mEmailsDataSource.deleteAllEntries();
                mTimer = null;
                mListeners.clear();
            }
        }

        @Override
        public List<Email> getAllEmails() throws RemoteException {
            synchronized (mLock) {
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
        public void addNewMailListener(NewMailListener aNewMailListener) throws RemoteException {
            synchronized (mLock) {
                mListeners.add(aNewMailListener);
            }
        }

        @Override
        public void removeNewMailListener(NewMailListener aNewMailListener) throws RemoteException {
            synchronized (mLock) {
                mListeners.remove(aNewMailListener);
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

    private void sendNotification(List<Email> aEmails) {
        Log.i(TAG, "Sending notification");
        if (aEmails != null) {
            String notificationText;
            String notificationTitle;
            if (aEmails.size() == 1) {
                Email email = aEmails.get(0);
                notificationTitle = "You've got new email!";
                notificationText = "Email from " + email.getSenderEmail();
            } else {
                notificationTitle = "You've got some new emails!";
                notificationText = aEmails.size() + " new emails";
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
        for (NewMailListener listener : mListeners) {
            try {
                listener.handleNewMail();
            } catch (RemoteException e) {
                Log.w(TAG, "Failed to notify listener " + listener, e);
            }
        }
    }
}
