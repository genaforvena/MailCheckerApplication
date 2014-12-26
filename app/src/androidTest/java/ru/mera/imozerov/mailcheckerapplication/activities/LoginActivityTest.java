package ru.mera.imozerov.mailcheckerapplication.activities;

import android.content.Context;
import android.content.Intent;
import android.test.ActivityUnitTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import ru.mera.imozerov.mailcheckerapplication.MailCheckerApplication;
import ru.mera.imozerov.mailcheckerapplication.R;
import ru.mera.imozerov.mailcheckerapplication.jobs.IJobListener;
import ru.mera.imozerov.mailcheckerapplication.mail.MailHelper;

import static ru.mera.imozerov.mailcheckerapplication.services.MailCheckerServiceTest.DUMMY_ACCOUNT;

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

        Context context = getInstrumentation().getTargetContext();
        startActivity(new Intent(context, LoginActivity.class), null, null);

        mLoginActivity = getActivity();
        mLoginActivity.mMailCheckerApplication = new MailCheckerApplication() {
            @Override
            public synchronized void setMailHelper(MailHelper mMailHelper) {
            }

            @Override
            public synchronized MailHelper getMailHelper() {
                return new MailHelper(DUMMY_ACCOUNT) {
                    @Override
                    public boolean isAbleToLogin() {
                        return true;
                    }
                };
            }
        };

        mEmailView = (AutoCompleteTextView) mLoginActivity.findViewById(R.id.email);
        mPasswordView = (EditText) mLoginActivity.findViewById(R.id.password);
        mProgressView = mLoginActivity.findViewById(R.id.login_progress);
        mEmailSignInButton = (Button) mLoginActivity.findViewById(R.id.email_sign_in_button);
    }

    @SmallTest
    public void testPreConditions() {
        assertNotNull(mEmailView);
        assertNotNull(mPasswordView);
        assertNotNull(mProgressView);
        assertNotNull(mEmailSignInButton);
    }

    @MediumTest
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

    @SmallTest
    public void testLoginFail_invalidCredentials_noPass() throws Throwable {
        mEmailView.setText("some@email.com");
        mPasswordView.setText("");
        mEmailSignInButton.performClick();

        assertNull(getStartedActivityIntent());
        assertEquals(mLoginActivity.getString(R.string.error_invalid_password), mPasswordView.getError().toString());
    }

    @SmallTest
    public void testLoginFail_invalidCredentials_noEmail() throws Throwable {
        mEmailView.setText("");
        mPasswordView.setText("password");
        mEmailSignInButton.performClick();

        assertNull(getStartedActivityIntent());
        assertEquals(mLoginActivity.getString(R.string.error_field_required), mEmailView.getError().toString());
    }

    @MediumTest
    public void testLifeCycleCreate() {
        getInstrumentation().callActivityOnStart(mLoginActivity);
        getInstrumentation().callActivityOnResume(mLoginActivity);
        getInstrumentation().callActivityOnPause(mLoginActivity);
        getInstrumentation().callActivityOnStop(mLoginActivity);
    }
}
