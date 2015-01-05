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
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import ru.mera.imozerov.mailcheckerapplication.MailCheckerApi;
import ru.mera.imozerov.mailcheckerapplication.NewMailListener;
import ru.mera.imozerov.mailcheckerapplication.R;
import ru.mera.imozerov.mailcheckerapplication.adapters.EmailListAdapter;
import ru.mera.imozerov.mailcheckerapplication.dto.Email;
import ru.mera.imozerov.mailcheckerapplication.dto.UserAccount;
import ru.mera.imozerov.mailcheckerapplication.services.MailCheckerService;
import ru.mera.imozerov.mailcheckerapplication.sharedPreferences.SharedPreferencesHelper;

public class EmailListActivity extends Activity {
    private static final String TAG = EmailListActivity.class.getName();

    private ListView mEmailListView;
    private EmailListAdapter mEmailListAdapter;
    private boolean mIsBound;
    private UserAccount mUserAccount;
    private MailCheckerApi mMailCheckerService;
    private NewMailListener.Stub mNewMailListener = new NewMailListener.Stub() {
        @Override
        public void handleNewMail() throws RemoteException {
            updateListView();
        }
    };

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG, "Service is connected");
            mIsBound = true;
            mMailCheckerService = MailCheckerApi.Stub.asInterface(service);
            try {
                mMailCheckerService.addNewMailListener(mNewMailListener);
                if (!mMailCheckerService.isLoggedIn()) {
                    mMailCheckerService.login(mUserAccount.getEmailAddress(), mUserAccount.getPassword());
                }
            } catch (RemoteException e) {
                Log.e(TAG, "Unable to login to service!", e);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG, "Service is disconnected");
            mIsBound = false;
            mMailCheckerService = null;
        }
    };
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

        mEmails = new ArrayList<>();

        mEmailListView = (ListView) findViewById(R.id.email_list_view);
        mEmailListAdapter = new EmailListAdapter(this, R.layout.email_row, mEmails);
        mEmailListView.setAdapter(mEmailListAdapter);

        Intent intent = new Intent(this, MailCheckerService.class);
        bindService(intent, mServiceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            mMailCheckerService.removeNewMailListener(mNewMailListener);
            unbindService(mServiceConnection);
        } catch (RemoteException e) {
            Log.e(TAG, "Unable to remove new mail listener", e);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_email_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void logout() {
        new SharedPreferencesHelper().removeUserAccount(this);
        if (mIsBound) {
            try {
                mMailCheckerService.removeNewMailListener(mNewMailListener);
            } catch (RemoteException e) {
                Log.e(TAG, "Unable to remove this as new email listener!", e);
            }
        }
    }

    private void updateListView() {
        try {
            mEmails = mMailCheckerService.getAllEmails();
            mEmailListAdapter.clear();
            mEmailListAdapter.addAll(mEmails);
            mEmailListAdapter.notifyDataSetChanged();
        } catch (RemoteException e) {
            Log.e(TAG, "Unable to get all emails", e);
        }
    }
}
