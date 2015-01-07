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

    public void setId(long aId) {
        mId = aId;
    }

    public String getSenderEmail() {
        return mSenderEmail;
    }

    public void setSenderEmail(String aSenderEmail) {
        mSenderEmail = aSenderEmail;
    }

    public List<String> getRecipientEmails() {
        return mRecipientEmails;
    }

    public void setRecipientEmails(List<String> aRecipientEmails) {
        mRecipientEmails = aRecipientEmails;
    }

    public Date getSentDate() {
        return mSentDate;
    }

    public void setSentDate(Date aSentDate) {
        mSentDate = aSentDate;
    }

    public String getSubject() {
        return mSubject;
    }

    public void setSubject(String aSubject) {
        mSubject = aSubject;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String aContent) {
        mContent = aContent;
    }

    public boolean isRead() {
        return mIsRead;
    }

    public void setRead(boolean aIsRead) {
        mIsRead = aIsRead;
    }

    protected Email(Parcel aIn) {
        mId = aIn.readLong();
        mSenderEmail = aIn.readString();
        if (aIn.readByte() == 0x01) {
            mRecipientEmails = new ArrayList<String>();
            aIn.readList(mRecipientEmails, String.class.getClassLoader());
        } else {
            mRecipientEmails = null;
        }
        long tmpMSentDate = aIn.readLong();
        mSentDate = tmpMSentDate != -1 ? new Date(tmpMSentDate) : null;
        mSubject = aIn.readString();
        mContent = aIn.readString();
        mIsRead = aIn.readByte() != 0x00;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel aDest, int aFlags) {
        aDest.writeLong(mId);
        aDest.writeString(mSenderEmail);
        if (mRecipientEmails == null) {
            aDest.writeByte((byte) (0x00));
        } else {
            aDest.writeByte((byte) (0x01));
            aDest.writeList(mRecipientEmails);
        }
        aDest.writeLong(mSentDate != null ? mSentDate.getTime() : -1L);
        aDest.writeString(mSubject);
        aDest.writeString(mContent);
        aDest.writeByte((byte) (mIsRead ? 0x01 : 0x00));
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Email> CREATOR = new Parcelable.Creator<Email>() {
        @Override
        public Email createFromParcel(Parcel aParcel) {
            return new Email(aParcel);
        }

        @Override
        public Email[] newArray(int aSize) {
            return new Email[aSize];
        }
    };
}
