package ru.mera.imozerov.mailcheckerapplication.dto;

import java.util.Date;
import java.util.List;

import javax.mail.Address;

/**
 * Created by imozerov on 23.12.2014.
 */
public class Email {
    private static final String TAG = Email.class.getName();

    private List<Address> mSenders;
    private Address mReceiver;
    private Date mSentDate;
    private String mSubject;
    private String mText;
    private boolean mIsUnread;

    public List<Address> getSenders() {
        return mSenders;
    }

    public void setSenders(List<Address> mSenders) {
        this.mSenders = mSenders;
    }

    public Address getReceiver() {
        return mReceiver;
    }

    public void setReceiver(Address mReceiver) {
        this.mReceiver = mReceiver;
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

    public String getText() {
        return mText;
    }

    public void setText(String mText) {
        this.mText = mText;
    }

    public boolean isUnread() {
        return mIsUnread;
    }

    public void setUnread(boolean mIsUnread) {
        this.mIsUnread = mIsUnread;
    }
}
