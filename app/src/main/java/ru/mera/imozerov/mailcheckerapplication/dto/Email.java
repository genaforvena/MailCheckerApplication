package ru.mera.imozerov.mailcheckerapplication.dto;

import java.util.Date;
import java.util.List;

import javax.mail.Address;

/**
 * Created by imozerov on 23.12.2014.
 */
public class Email {
    private static final String TAG = Email.class.getName();

    private long mId;
    private Address mSender;
    private List<Address> mRecipients;
    private Date mSentDate;
    private String mSubject;
    private String mContent;
    private boolean mIsUnread;

    public long getId() {
        return mId;
    }

    public void setId(long mId) {
        this.mId = mId;
    }

    public Address getSenders() {
        return mSender;
    }

    public void setSenders(Address mSender) {
        this.mSender = mSender;
    }

    public List<Address> getRecipients() {
        return mRecipients;
    }

    public void setRecipients(List<Address> mRecipients) {
        this.mRecipients = mRecipients;
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

    public boolean isUnread() {
        return mIsUnread;
    }

    public void setUnread(boolean mIsUnread) {
        this.mIsUnread = mIsUnread;
    }
}
