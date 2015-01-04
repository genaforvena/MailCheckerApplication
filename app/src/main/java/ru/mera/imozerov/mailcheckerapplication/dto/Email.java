package ru.mera.imozerov.mailcheckerapplication.dto;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by imozerov on 23.12.2014.
 */
public class Email implements Parcelable {
    private static final String TAG = Email.class.getName();

    private long mId;
    private String mSenderEmail;
    private List<String> mRecipientEmails;
    private Date mSentDate;
    private String mSubject;
    private String mContent;
    private boolean mIsRead;

    public Email() {}

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

    protected Email(Parcel in) {
        mId = in.readLong();
        mSenderEmail = in.readString();
        if (in.readByte() == 0x01) {
            mRecipientEmails = new ArrayList<String>();
            in.readList(mRecipientEmails, String.class.getClassLoader());
        } else {
            mRecipientEmails = null;
        }
        long tmpMSentDate = in.readLong();
        mSentDate = tmpMSentDate != -1 ? new Date(tmpMSentDate) : null;
        mSubject = in.readString();
        mContent = in.readString();
        mIsRead = in.readByte() != 0x00;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mId);
        dest.writeString(mSenderEmail);
        if (mRecipientEmails == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(mRecipientEmails);
        }
        dest.writeLong(mSentDate != null ? mSentDate.getTime() : -1L);
        dest.writeString(mSubject);
        dest.writeString(mContent);
        dest.writeByte((byte) (mIsRead ? 0x01 : 0x00));
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Email> CREATOR = new Parcelable.Creator<Email>() {
        @Override
        public Email createFromParcel(Parcel in) {
            return new Email(in);
        }

        @Override
        public Email[] newArray(int size) {
            return new Email[size];
        }
    };
}
