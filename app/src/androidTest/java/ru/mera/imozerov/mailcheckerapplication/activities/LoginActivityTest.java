package ru.mera.imozerov.mailcheckerapplication.activities;

import android.content.Intent;
import android.test.ActivityUnitTestCase;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import ru.mera.imozerov.mailcheckerapplication.R;
import ru.mera.imozerov.mailcheckerapplication.jobs.IJobListener;

/**
 * Created by imozerov on 25.12.2014.
 */
public class LoginActivityTest extends ActivityUnitTestCase<LoginActivity>{
    LoginActivity mLoginActivity;
    EditText mPasswordView;
    View mProgressView;
    AutoCompleteTextView mEmailView;
    private Button mEmailSignInButton;

    public LoginActivityTest() {
        super(LoginActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        Intent intent = new Intent(getInstrumentation().getTargetContext(),
                LoginActivity.class);
        startActivity(intent, null, null);

        mLoginActivity = getActivity();

        mEmailView = (AutoCompleteTextView) mLoginActivity.findViewById(R.id.email);
        mPasswordView = (EditText) mLoginActivity.findViewById(R.id.password);
        mProgressView = mLoginActivity.findViewById(R.id.login_progress);
        mEmailSignInButton = (Button) mLoginActivity.findViewById(R.id.email_sign_in_button);
    }

    public void testPreConditions() {
        assertNotNull(mEmailView);
        assertNotNull(mPasswordView);
        assertNotNull(mProgressView);
        assertNotNull(mEmailSignInButton);
    }

    public void testLoginSuccess() throws Throwable {
        final CountDownLatch latch = new CountDownLatch(1);

        mLoginActivity.setListener(new IJobListener() {
            @Override
            public void executionDone() {
                latch.countDown();
            }
        });

        mEmailView.setText("some@email.com");
        mPasswordView.setText("password");
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                mEmailSignInButton.performClick();
            }
        });

        boolean await = latch.await(30, TimeUnit.SECONDS);

        assertTrue(await);
        assertNotNull(getStartedActivityIntent());
    }

    public void testLoginFail_invalidCredentials() throws Throwable {
        final CountDownLatch latch = new CountDownLatch(1);

        mLoginActivity.setListener(new IJobListener() {
            @Override
            public void executionDone() {
                latch.countDown();
            }
        });

        mEmailView.setText("some@email.com");
        mPasswordView.setText("");
        runTestOnUiThread(new Runnable() {
            @Override
            public void run() {
                mEmailSignInButton.performClick();
            }
        });

        boolean await = latch.await(30, TimeUnit.SECONDS);

        assertTrue(await);
        assertNull(getStartedActivityIntent());
        assertNotNull(mPasswordView.getError());
    }
}
