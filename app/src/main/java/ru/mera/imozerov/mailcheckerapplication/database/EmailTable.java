package ru.mera.imozerov.mailcheckerapplication.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by imozerov on 03.01.2015.
 */
public final class EmailTable {
    private static final String TAG = EmailTable.class.getName();

    public static final String TABLE_NAME = "emails";
    public static final String COLUMN_NAME_EMAIL_ID = "_id";
    public static final String COLUMN_NAME_SENDER_EMAIL = "sender_email";
    public static final String COLUMN_NAME_SENT_DATE = "sent";
    public static final String COLUMN_NAME_SUBJECT = "subject";
    public static final String COLUMN_NAME_CONTENT = "content";
    public static final String COLUMN_NAME_IS_READ = "is_read";

    public static final String[] allColumns = { COLUMN_NAME_EMAIL_ID, COLUMN_NAME_SENDER_EMAIL, COLUMN_NAME_SENT_DATE, COLUMN_NAME_SUBJECT, COLUMN_NAME_CONTENT, COLUMN_NAME_IS_READ};

    private static final String SQL_CREATE_EMAIL_DATABASE = "CREATE TABLE " + TABLE_NAME
            + " (" + COLUMN_NAME_EMAIL_ID + EmailDatabaseHelper.INTEGER_TYPE + " PRIMARY KEY AUTOINCREMENT" + EmailDatabaseHelper.COMMA_SEP
            + COLUMN_NAME_CONTENT + EmailDatabaseHelper.TEXT_TYPE + EmailDatabaseHelper.COMMA_SEP
            + COLUMN_NAME_SENDER_EMAIL + EmailDatabaseHelper.TEXT_TYPE + EmailDatabaseHelper.COMMA_SEP
            + COLUMN_NAME_SENT_DATE + EmailDatabaseHelper.INTEGER_TYPE + EmailDatabaseHelper.COMMA_SEP
            + COLUMN_NAME_SUBJECT + EmailDatabaseHelper.TEXT_TYPE + EmailDatabaseHelper.COMMA_SEP
            + COLUMN_NAME_IS_READ + EmailDatabaseHelper.INTEGER_TYPE + ")";

    private static final String SQL_DELETE_EMAIL_DATABASE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public static void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_EMAIL_DATABASE);
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "Upgrading db from " + oldVersion + " to " + newVersion);
        db.execSQL(SQL_DELETE_EMAIL_DATABASE);
        onCreate(db);
    }
}
