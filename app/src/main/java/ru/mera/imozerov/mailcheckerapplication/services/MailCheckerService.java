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

import ru.mera.imozerov.mailcheckerapplication.MailCheckerApi;
import ru.mera.imozerov.mailcheckerapplication.NewMailListener;
import ru.mera.imozerov.mailcheckerapplication.R;
import ru.mera.imozerov.mailcheckerapplication.activities.EmailListActivity;
import ru.mera.imozerov.mailcheckerapplication.database.EmailsDataSource;
import ru.mera.imozerov.mailcheckerapplication.dto.Email;
import ru.mera.imozerov.mailcheckerapplication.dto.UserAccount;
import ru.mera.imozerov.mailcheckerapplication.exceptions.EmptyCredentialsException;
import ru.mera.imozerov.mailcheckerapplication.mail.MailHelper;
import ru.mera.imozerov.mailcheckerapplication.sharedPreferences.SharedPreferencesHelper;

public class MailCheckerService extends Service {
    private static final String TAG = MailCheckerService.class.getName();

    public static final String INTENT_ACTION_NAME = "ru.mera.imozerov.mailcheckerapplication.action.START_MAIL_CHECKER_SERVICE";
    public static final long DOWNLOAD_EMAIL_TASK_PERIOD = 60 * 1000L;
    public static final long DOWNLOAD_EMAIL_TASK_DELAY = 1000L;

    private List<NewMailListener> mListeners = new ArrayList<NewMailListener>();
    private MailCheckerApi.Stub mMailCheckerApi = new MailCheckerApiImplementation();
    private TimerTask mDownloadNewEmailsTask = new DownloadNewEmailsTask();
    private SharedPreferencesHelper mSharedPreferencesHelper = new SharedPreferencesHelper();
    private EmailsDataSource mEmailsDataSource = new EmailsDataSource(this);
    private Object mLock = new Object();

    private MailHelper mMailHelper;
    private Timer mTimer;
    private NotificationManager mNotificationManager;

    @Override
    public IBinder onBind(Intent aIntent) {
        Log.i(TAG, "Binding service.");
        return mMailCheckerApi;
    }

    @Override
    public int onStartCommand(Intent aIntent, int aFlags, int aStartId) {
        Log.i(TAG, "Starting service.");
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        if (hasCredentials()) {
            scheduleDownloadNewEmailsTask();
        }
    }

    private void scheduleDownloadNewEmailsTask() {
        UserAccount userAccount = mSharedPreferencesHelper.getUserAccount(this);
        setMailHelper(new MailHelper(userAccount));
        mTimer = new Timer("MailCheckerTimer");
        mTimer.schedule(mDownloadNewEmailsTask, DOWNLOAD_EMAIL_TASK_DELAY, DOWNLOAD_EMAIL_TASK_PERIOD);
    }

    private boolean hasCredentials() {
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

    private void notifyListeners() {
        for (NewMailListener listener : mListeners) {
            try {
                listener.handleNewMail();
            } catch (RemoteException e) {
                Log.w(TAG, "Failed to notify listener " + listener + ".", e);
            }
        }
    }

    class MailCheckerApiImplementation extends MailCheckerApi.Stub {

        @Override
        public boolean hasCredentials() throws RemoteException {
            synchronized (mLock) {
                return MailCheckerService.this.hasCredentials();
            }
        }

        @Override
        public void login(String aLogin, String aPassword) throws RemoteException {
            synchronized (mLock) {
                Log.i(TAG, "Logging in to " + aLogin);
                try {
                    UserAccount userAccount = new UserAccount(aLogin, aPassword);
                    mSharedPreferencesHelper.saveUserAccount(MailCheckerService.this, userAccount);
                    scheduleDownloadNewEmailsTask();
                } catch (EmptyCredentialsException e) {
                    Log.e(TAG, "Some of credentials passed is empty!", e);
                }
            }
        }

        @Override
        public void logout() throws RemoteException {
            synchronized (mLock) {
                Log.i(TAG, "Logging out.");
                mSharedPreferencesHelper.removeUserAccount(MailCheckerService.this);
                mSharedPreferencesHelper.removeLastCheckDate(MailCheckerService.this);
                if (mTimer != null) {
                    mTimer.cancel();
                    mTimer = null;
                }
                mListeners.clear();
                try {
                    mEmailsDataSource.open();
                    mEmailsDataSource.deleteAllEntries();
                } catch (SQLException e) {
                    Log.e(TAG, "Unable to delete all db entries!", e);
                } finally {
                    mEmailsDataSource.close();
                }
            }
        }

        @Override
        public List<Email> getAllEmails() throws RemoteException {
            synchronized (mLock) {
                Log.i(TAG, "Getting all emails.");
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
                if (aNewMailListener == null) {
                    Log.e(TAG, "Null listener is passed!");
                    return;
                }
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

    class DownloadNewEmailsTask extends TimerTask {
        private static final int NOTIFICATION_ID = 12345;

        private List<Email> mDownloadedEmails;

        @Override
        public void run() {
            Log.i(TAG, "Checking for e-mails.");
            if (mMailHelper == null) {
                Log.w(TAG, "MailHelper is not set. Cannot check new emails!");
                return;
            }

            Date nextCheckDate = new Date();
            mDownloadedEmails = mMailHelper.getEmailsFromInbox(mSharedPreferencesHelper.getNextCheckDate(MailCheckerService.this));
            mSharedPreferencesHelper.saveNextCheckDate(MailCheckerService.this, nextCheckDate);

            if (mDownloadedEmails.size() == 0) {
                Log.i(TAG, "Attempted to get emails. But there are no.");
                return;
            }

            Log.i(TAG, "Got " + mDownloadedEmails.size() + " emails.");
            try {
                mEmailsDataSource.open();
                for (Email email : mDownloadedEmails) {
                    mEmailsDataSource.saveEmail(email);
                }
                notifyListeners();
                sendNotification();
            } catch (SQLException e) {
                Log.e(TAG, "Unable to save emails to db!", e);
            } finally {
                mEmailsDataSource.close();
                mDownloadedEmails = null;
            }
        }

        private void sendNotification() {
            Log.i(TAG, "Sending notification");
            String notificationTitle = getResources().getQuantityString(R.plurals.numberOfEmailsReceivedNotificationTitle, mDownloadedEmails.size(), mDownloadedEmails.size());

            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(MailCheckerService.this)
                            .setSmallIcon(R.drawable.ic_launcher)
                            .setContentTitle(notificationTitle)
                            .setAutoCancel(true);

            Intent targetIntent = new Intent(MailCheckerService.this, EmailListActivity.class);
            PendingIntent contentIntent = PendingIntent.getActivity(MailCheckerService.this, 0, targetIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(contentIntent);
            if (mNotificationManager == null) {
                mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            }
            mNotificationManager.notify(NOTIFICATION_ID, builder.build());
        }
    }
}
