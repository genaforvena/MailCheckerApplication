package ru.mera.imozerov.mailcheckerapplication.activities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ru.mera.imozerov.mailcheckerapplication.MailCheckerApi;
import ru.mera.imozerov.mailcheckerapplication.R;
import ru.mera.imozerov.mailcheckerapplication.adapters.EmailListAdapter;
import ru.mera.imozerov.mailcheckerapplication.dto.Email;
import ru.mera.imozerov.mailcheckerapplication.dto.UserAccount;
import ru.mera.imozerov.mailcheckerapplication.services.MailCheckerService;
import ru.mera.imozerov.mailcheckerapplication.sharedPreferences.SharedPreferencesHelper;

public class EmailListActivity extends Activity {
    private static final String TAG = EmailListActivity.class.getName();
    public static final String EMAIL_EXTRA = EmailListActivity.class.getName() + "Email";

    private ListView mEmailListView;
    private EmailListAdapter mEmailListAdapter;

    private SharedPreferencesHelper mSharedPreferencesHelper = new SharedPreferencesHelper();

    private boolean mIsBound;
    private UserAccount mUserAccount;
    private MailCheckerApi mMailCheckerService;
    private NewMailListener.Stub mNewMailListener;
    private ServiceConnection mServiceConnection;
    private List<Email> mEmails;

    @Override
    protected void onCreate(Bundle aSavedInstanceState) {
        super.onCreate(aSavedInstanceState);
        setContentView(R.layout.activity_email_list);

        mUserAccount = mSharedPreferencesHelper.getUserAccount(this);
        if (mUserAccount == null) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            mServiceConnection = new MailCheckerServiceConnection();
            mNewMailListener = new NewMailListener();
            Intent intent = new Intent(this, MailCheckerService.class);
            bindService(intent, mServiceConnection, BIND_AUTO_CREATE);
        }

        mEmails = new ArrayList<Email>();

        mEmailListView = (ListView) findViewById(R.id.email_list_view);
        mEmailListAdapter = new EmailListAdapter(this, R.layout.email_row, mEmails);
        mEmailListView.setAdapter(mEmailListAdapter);
        mEmailListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Email email = mEmails.get(position);
                Intent intent = new Intent(EmailListActivity.this, EmailViewActivity.class);
                intent.putExtra(EMAIL_EXTRA, email);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mIsBound) {
            try {
                mMailCheckerService.removeNewMailListener(mNewMailListener);
                unbindService(mServiceConnection);
            } catch (RemoteException e) {
                Log.w(TAG, "Unable to remove new mail listener", e);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu aMenu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_email_list, aMenu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem aMenuItem) {
        switch (aMenuItem.getItemId()) {
            case R.id.action_logout:
                logout();
                return true;
            default:
                return super.onOptionsItemSelected(aMenuItem);
        }
    }

    private void logout() {
        if (mIsBound) {
            try {
                mMailCheckerService.logout();
            } catch (RemoteException e) {
                Log.w(TAG, "Unable to log out!", e);
            }
        } else {
            mSharedPreferencesHelper.removeUserAccount(this);
        }
        finish();
        startActivity(new Intent(this, LoginActivity.class));
    }

    private void updateListView() {
        try {
            mEmails = mMailCheckerService.getAllEmails();
            Collections.reverse(mEmails);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mEmailListAdapter.clear();
                    mEmailListAdapter.addAll(mEmails);
                    mEmailListAdapter.notifyDataSetChanged();
                }
            });
        } catch (RemoteException e) {
            Log.w(TAG, "Unable to get all emails", e);
        }
    }

    private class MailCheckerServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName aComponentName, IBinder aService) {
            Log.i(TAG, "Service is connected");
            mIsBound = true;
            mMailCheckerService = MailCheckerApi.Stub.asInterface(aService);
            try {
                mMailCheckerService.addNewMailListener(mNewMailListener);
                if (!mMailCheckerService.hasCredentials()) {
                    mMailCheckerService.login(mUserAccount.getEmailAddress(), mUserAccount.getPassword());
                } else {
                    updateListView();
                }
            } catch (RemoteException e) {
                Log.w(TAG, "Unable to login to service!", e);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName aComponentName) {
            Log.i(TAG, "Service is disconnected");
            mIsBound = false;
            mMailCheckerService = null;
        }
    }

    private class NewMailListener extends ru.mera.imozerov.mailcheckerapplication.NewMailListener.Stub {
        @Override
        public void handleNewMail() throws RemoteException {
            updateListView();
        }
    }
}
