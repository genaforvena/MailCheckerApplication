package ru.mera.imozerov.mailcheckerapplication.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import ru.mera.imozerov.mailcheckerapplication.R;
import ru.mera.imozerov.mailcheckerapplication.dto.Email;

public class EmailViewActivity extends Activity {
    private static final String TAG = EmailViewActivity.class.getName();

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
        mEmail = intent.getParcelableExtra(EmailListActivity.EMAIL_EXTRA);

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
