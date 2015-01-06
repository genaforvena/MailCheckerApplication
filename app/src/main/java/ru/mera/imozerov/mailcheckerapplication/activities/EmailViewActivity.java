package ru.mera.imozerov.mailcheckerapplication.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import ru.mera.imozerov.mailcheckerapplication.R;
import ru.mera.imozerov.mailcheckerapplication.dto.Email;
import ru.mera.imozerov.mailcheckerapplication.sharedPreferences.SharedPreferencesHelper;

public class EmailViewActivity extends Activity {
    private static final String TAG = EmailViewActivity.class.getName();

    private SharedPreferencesHelper mSharedPreferencesHelper = new SharedPreferencesHelper();

    private TextView mSubjectView;
    private TextView mSentTimeView;
    private TextView mSenderView;
    private TextView mContentView;
    private Email mEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_view);

        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            mEmail = intent.getParcelableExtra(EmailListActivity.EMAIL_EXTRA);
            mSharedPreferencesHelper.saveLastSeenEmail(this, mEmail);
        } else {
            mEmail = mSharedPreferencesHelper.getLastSeenEmail(this);
        }

        mSubjectView = (TextView) findViewById(R.id.email_view_subject);
        mSentTimeView = (TextView) findViewById(R.id.email_view_sent_time);
        mSenderView = (TextView) findViewById(R.id.email_view_sender);
        mContentView = (TextView) findViewById(R.id.email_view_content);

        mContentView.setText(mEmail.getContent());
        mSenderView.setText(mEmail.getSenderEmail());
        mSentTimeView.setText(mEmail.getSentDate().toString());
        mSubjectView.setText(mEmail.getSubject());
    }
}
