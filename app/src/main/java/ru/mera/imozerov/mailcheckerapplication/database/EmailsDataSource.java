package ru.mera.imozerov.mailcheckerapplication.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ru.mera.imozerov.mailcheckerapplication.dto.Email;

/**
 * Created by imozerov on 03.01.2015.
 */
public class EmailsDataSource {
    private static final String TAG = EmailsDataSource.class.getName();

    private SQLiteDatabase mDatabase;
    private EmailDatabaseHelper mEmailDatabaseHelper;

    public EmailsDataSource(Context context) {
        mEmailDatabaseHelper = new EmailDatabaseHelper(context);
    }

    public void open() throws SQLException {
        mDatabase = mEmailDatabaseHelper.getWritableDatabase();
    }

    public void close() {
        mEmailDatabaseHelper.close();
    }

    public void deleteAllEntries() {
        mEmailDatabaseHelper.deleteAllEntries(mDatabase);
    }

    public void saveEmail(Email aEmail) {
        if (aEmail == null) {
            Log.w(TAG, "email is null!");
            return;
        }

        ContentValues values = new ContentValues();
        values.put(EmailTable.COLUMN_NAME_CONTENT, aEmail.getContent());
        values.put(EmailTable.COLUMN_NAME_SENDER_EMAIL, aEmail.getSenderEmail());
        values.put(EmailTable.COLUMN_NAME_SENT_DATE, aEmail.getSentDate().getTime());
        values.put(EmailTable.COLUMN_NAME_SUBJECT, aEmail.getSubject());
        values.put(EmailTable.COLUMN_NAME_IS_READ, aEmail.isRead() ? 1 : 0);

        long emailId = mDatabase.insert(EmailTable.TABLE_NAME, null, values);

        // TODO Skip recipients just for now. fix this
//        for (String recipientEmail : email.getRecipientEmails()) {
//            ContentValues recipientsValues = new ContentValues();
//            recipientsValues.put(RecipientTable.COLUMN_NAME_EMAIL_ID, emailId);
//            recipientsValues.put(RecipientTable.COLUMN_NAME_RECIPIENT_EMAIL, recipientEmail);
//            mDatabase.insert(RecipientTable.TABLE_NAME, null, recipientsValues);
//        }
    }

    public List<Email> getAllEmails() {
        List<Email> emails = new ArrayList<Email>();

        Cursor emailCursor = mDatabase.query(EmailTable.TABLE_NAME, EmailTable.allColumns, null, null, null, null, null);

        emailCursor.moveToFirst();
        while (!emailCursor.isAfterLast()) {
            Email email = cursorToEmail(emailCursor);
            emails.add(email);
            emailCursor.moveToNext();
        }

        emailCursor.close();
        return emails;
    }

    private Email cursorToEmail(Cursor aEmailCursor) {
        Email email = new Email();
        email.setId(aEmailCursor.getLong(aEmailCursor.getColumnIndex(EmailTable.COLUMN_NAME_EMAIL_ID)));
        email.setSubject(aEmailCursor.getString(aEmailCursor.getColumnIndex(EmailTable.COLUMN_NAME_SUBJECT)));
        email.setSentDate(new Date(aEmailCursor.getLong(aEmailCursor.getColumnIndex(EmailTable.COLUMN_NAME_SENT_DATE))));
        email.setContent(aEmailCursor.getString(aEmailCursor.getColumnIndex(EmailTable.COLUMN_NAME_CONTENT)));
        email.setSenderEmail(aEmailCursor.getString(aEmailCursor.getColumnIndex(EmailTable.COLUMN_NAME_SENDER_EMAIL)));
        email.setRead(aEmailCursor.getInt(aEmailCursor.getColumnIndex(EmailTable.COLUMN_NAME_IS_READ)) == 1 ? true : false);
        email.setRecipientEmails(new ArrayList<String>());

//        Cursor recipientCursor = mDatabase.query(RecipientTable.TABLE_NAME, RecipientTable.allColumns, RecipientTable.COLUMN_NAME_EMAIL_ID + "=?", new String[] {String.valueOf(email.getId())}, null, null, null);
//        recipientCursor.moveToFirst();
//        while (!recipientCursor.isAfterLast()) {
//            email.getRecipientEmails().add(recipientCursor.getString(recipientCursor.getColumnIndex(RecipientTable.COLUMN_NAME_RECIPIENT_EMAIL)));
//        }
//        recipientCursor.close();
        return email;
    }
}
