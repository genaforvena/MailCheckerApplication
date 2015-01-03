package ru.mera.imozerov.mailcheckerapplication.dto;

import java.util.Date;
import java.util.List;

/**
 * Created by imozerov on 23.12.2014.
 */
public class Email {
    private static final String TAG = Email.class.getName();

    private long mId;
    private String mSenderEmail;
    private List<String> mRecipientEmails;
    private Date mSentDate;
    private String mSubject;
    private String mContent;
    private boolean mIsRead;

    public long getId() {
        return mId;
    }

    public void setId(long mId) {
        this.mId = mId;
    }

    public String getSenderEmail() {
        return mSenderEmail;
    }

    public void setSenderEmail(String mSender) {
        this.mSenderEmail = mSender;
    }

    public List<String> getRecipientEmails() {
        return mRecipientEmails;
    }

    public void setRecipientEmails(List<String> mRecipients) {
        this.mRecipientEmails = mRecipients;
    }

    public Date getSentDate() {
        return mSentDate;
    }

    public void setSentDate(Date mSentDate) {
        this.mSentDate = mSentDate;
    }

    public String getSubject() {
        return mSubject;
    }

    public void setSubject(String mSubject) {
        this.mSubject = mSubject;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String mText) {
        this.mContent = mText;
    }

    public boolean isRead() {
        return mIsRead;
    }

    public void setRead(boolean mIsRead) {
        this.mIsRead = mIsRead;
    }
}
