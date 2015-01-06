package ru.mera.imozerov.mailcheckerapplication.activities;

import android.content.Context;
import android.content.Intent;
import android.test.ActivityUnitTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.widget.TextView;

import java.util.Date;

import ru.mera.imozerov.mailcheckerapplication.R;
import ru.mera.imozerov.mailcheckerapplication.dto.Email;
import ru.mera.imozerov.mailcheckerapplication.sharedPreferences.SharedPreferencesHelper;

/**
 * Created by imozerov on 06.01.2015.
 */
public class EmailViewActivityTest extends ActivityUnitTestCase<EmailViewActivity> {
    private EmailViewActivity mEmailViewActivity;
    private SharedPreferencesHelper mSharedPreferencesHelper = new SharedPreferencesHelper();

    private Context mContext;
    private TextView mSubjectView;
    private TextView mContentView;
    private TextView mSentDateView;
    private TextView mSenderView;

    private static Email sEmail;

    static {
        sEmail = new Email();
        sEmail.setSentDate(new Date());
        sEmail.setSubject("Subject");
        sEmail.setSenderEmail("email@mail.com");
        sEmail.setContent("Content");
    }

    public EmailViewActivityTest() {
        super(EmailViewActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        mContext = getInstrumentation().getTargetContext();
        Intent intent = new Intent(mContext, EmailViewActivity.class);
        intent.putExtra(EmailListActivity.EMAIL_EXTRA, sEmail);
        startActivity(intent, null, null);

        mEmailViewActivity = getActivity();

        mSubjectView = (TextView) mEmailViewActivity.findViewById(R.id.email_view_subject);
        mContentView = (TextView) mEmailViewActivity.findViewById(R.id.email_view_content);
        mSentDateView = (TextView) mEmailViewActivity.findViewById(R.id.email_view_sent_time);
        mSenderView = (TextView) mEmailViewActivity.findViewById(R.id.email_view_sender);
    }

    public void testPreconditions() {
        assertNotNull(mSubjectView);
        assertNotNull(mContentView);
        assertNotNull(mSentDateView);
        assertNotNull(mSenderView);
        assertNotNull(mEmailViewActivity);
    }

    public void testUi() {
        assertEquals(sEmail.getSubject(), mSubjectView.getText().toString());
        assertEquals(sEmail.getContent(), mContentView.getText().toString());
        assertEquals(sEmail.getSenderEmail(), mSenderView.getText().toString());
        assertEquals(sEmail.getSentDate().toString(), mSentDateView.getText().toString());
    }

    public void test_saveLastSeenEmail() {
        getInstrumentation().callActivityOnStart(mEmailViewActivity);
        getInstrumentation().callActivityOnResume(mEmailViewActivity);
        getInstrumentation().callActivityOnPause(mEmailViewActivity);
        getInstrumentation().callActivityOnStop(mEmailViewActivity);
        getInstrumentation().callActivityOnStart(mEmailViewActivity);

        assertEquals(sEmail.getSubject(), mSubjectView.getText().toString());
        assertEquals(sEmail.getContent(), mContentView.getText().toString());
        assertEquals(sEmail.getSenderEmail(), mSenderView.getText().toString());
        assertEquals(sEmail.getSentDate().toString(), mSentDateView.getText().toString());

        Email savedEmail = mSharedPreferencesHelper.getLastSeenEmail(mContext);
        assertEquals(sEmail.getSubject(),savedEmail.getSubject());
        assertEquals(sEmail.getContent(), savedEmail.getContent());
        assertEquals(sEmail.getSenderEmail(), savedEmail.getSenderEmail());
        assertEquals(sEmail.getSentDate(), savedEmail.getSentDate());
    }

    @MediumTest
    public void testLifeCycleCreate() {
        getInstrumentation().callActivityOnStart(mEmailViewActivity);
        getInstrumentation().callActivityOnResume(mEmailViewActivity);
        getInstrumentation().callActivityOnPause(mEmailViewActivity);
        getInstrumentation().callActivityOnStop(mEmailViewActivity);
    }
}
