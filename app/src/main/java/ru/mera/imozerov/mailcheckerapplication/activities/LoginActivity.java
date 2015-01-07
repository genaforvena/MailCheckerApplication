package ru.mera.imozerov.mailcheckerapplication.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import ru.mera.imozerov.mailcheckerapplication.MailCheckerApplication;
import ru.mera.imozerov.mailcheckerapplication.R;
import ru.mera.imozerov.mailcheckerapplication.dto.UserAccount;
import ru.mera.imozerov.mailcheckerapplication.exceptions.EmptyCredentialsException;
import ru.mera.imozerov.mailcheckerapplication.jobs.IJobListener;
import ru.mera.imozerov.mailcheckerapplication.mail.MailHelper;
import ru.mera.imozerov.mailcheckerapplication.services.MailCheckerService;
import ru.mera.imozerov.mailcheckerapplication.sharedPreferences.SharedPreferencesHelper;

public class LoginActivity extends Activity {
    private static final String TAG = LoginActivity.class.getName();
    MailCheckerApplication mMailCheckerApplication;
    SharedPreferencesHelper mSharedPreferencesHelper = new SharedPreferencesHelper();
    private CheckLoginTask mAuthTask = null;
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private Button mEmailSignInButton;
    private IJobListener mListener;

    @Override
    protected void onCreate(Bundle aSavedInstanceState) {
        super.onCreate(aSavedInstanceState);
        setContentView(R.layout.activity_login);

        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        mEmailView.setError(null);
        mPasswordView.setError(null);

        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(password) || !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            mAuthTask = new CheckLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String aEmail) {
        try {
            InternetAddress internetAddress = new InternetAddress(aEmail);
            internetAddress.validate();
            return true;
        } catch (AddressException e) {
            Log.w(TAG, "Email (" + aEmail + ") is not valid!", e);
            return false;
        }
    }

    private boolean isPasswordValid(String aPassword) {
        return aPassword.length() > 4;
    }

    // for testing purposes
    void setListener(IJobListener aJobListener) {
        this.mListener = aJobListener;
    }

    private void showProgress(boolean show) {
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private class CheckLoginTask extends AsyncTask<Void, Void, Boolean> {
        private final String mEmail;
        private final String mPassword;
        private UserAccount mUserAccount;

        CheckLoginTask(String aEmail, String aPassword) {
            mEmail = aEmail;
            mPassword = aPassword;
        }

        @Override
        protected Boolean doInBackground(Void... aParams) {
            try {
                mUserAccount = new UserAccount(mEmail, mPassword);

                MailHelper mailHelper = new MailHelper(mUserAccount);
                if (mMailCheckerApplication == null) {
                    mMailCheckerApplication = ((MailCheckerApplication) getApplication());
                }
                mMailCheckerApplication.setMailHelper(mailHelper);
                return mMailCheckerApplication.getMailHelper().isAbleToLogin();
            } catch (EmptyCredentialsException e) {
                Log.e(TAG, "Empty credentials passed!", e);
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean aSuccess) {
            mAuthTask = null;
            showProgress(false);

            if (aSuccess) {
                mSharedPreferencesHelper.saveUserAccount(LoginActivity.this, mUserAccount);
                finish();
                startService(new Intent(MailCheckerService.INTENT_ACTION_NAME));
                startActivity(new Intent(LoginActivity.this, EmailListActivity.class));
            } else {
                mUserAccount = null;
                mMailCheckerApplication.setMailHelper(null);
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }

            if (mListener != null) {
                mListener.executionDone();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}



