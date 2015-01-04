package ru.mera.imozerov.mailcheckerapplication.activities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import ru.mera.imozerov.mailcheckerapplication.BuildConfig;
import ru.mera.imozerov.mailcheckerapplication.MailCheckerApi;
import ru.mera.imozerov.mailcheckerapplication.NewMailListener;
import ru.mera.imozerov.mailcheckerapplication.R;
import ru.mera.imozerov.mailcheckerapplication.adapters.EmailListAdapter;
import ru.mera.imozerov.mailcheckerapplication.dto.Email;
import ru.mera.imozerov.mailcheckerapplication.dto.UserAccount;
import ru.mera.imozerov.mailcheckerapplication.services.MailCheckerService;
import ru.mera.imozerov.mailcheckerapplication.sharedPreferences.SharedPreferencesHelper;

public class EmailListActivity extends Activity implements NewMailListener {
    private static final String TAG = EmailListActivity.class.getName();

    private ListView mEmailListView;
    private EmailListAdapter mEmailListAdapter;
    private ServiceConnection mServiceConnection;
    private UserAccount mUserAccount;
    private MailCheckerApi mMailCheckerService;
    private List<Email> mEmails;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_list);

        mUserAccount = new SharedPreferencesHelper().getUserAccount(this);
        if (mUserAccount == null) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.i(TAG, "Service is connected");
                mMailCheckerService = (MailCheckerApi) service;
                try {
                    mEmails = mMailCheckerService.getAllEmails();
                    mEmailListAdapter.clear();
                    mEmailListAdapter.addAll(mEmails);
                    mEmailListAdapter.notifyDataSetChanged();
                } catch (RemoteException e) {
                    Log.e(TAG, "Unable to get all emails from service!", e);
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "Service is disconnected");
                }
            }
        };

        Intent intent = new Intent(this, MailCheckerService.class);
        intent.putExtra(MailCheckerService.LOGIN, mUserAccount.getEmailAddress());
        intent.putExtra(MailCheckerService.PASSWORD, mUserAccount.getPassword());
        bindService(intent, mServiceConnection, 0);

        mEmails = new ArrayList<>();

        mEmailListView = (ListView) findViewById(R.id.email_list_view);
        mEmailListAdapter = new EmailListAdapter(this, R.layout.email_row, mEmails);
        mEmailListView.setAdapter(mEmailListAdapter);
    }

    @Override
    public void handleNewMail() throws RemoteException {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "New email arrived!");
        }
    }

    @Override
    public IBinder asBinder() {
        return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
    }
}
