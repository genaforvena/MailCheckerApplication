package ru.mera.imozerov.mailcheckerapplication.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by imozerov on 29.12.2014.
 */
public class EmailDatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = EmailDatabaseHelper.class.getName();

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "MailChecker.db";
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    public static final String INTEGER_TYPE = " INTEGER";
    private static final String SQL_CREATE_EMAIL_DATABASE = "CREATE TABLE " + DatabaseSchema.Email.TABLE_NAME
            + " (" + DatabaseSchema.Email.COLUMN_NAME_EMAIL_ID + INTEGER_TYPE + " PRIMARY KEY AUTOINCREMENT, " + COMMA_SEP
            + DatabaseSchema.Email.COLUMN_NAME_CONTENT + TEXT_TYPE + COMMA_SEP
            + DatabaseSchema.Email.COLUMN_NAME_SENDER_EMAIL + TEXT_TYPE + COMMA_SEP
            + DatabaseSchema.Email.COLUMN_NAME_SENT_DATE + INTEGER_TYPE + COMMA_SEP
            + DatabaseSchema.Email.COLUMN_NAME_SUBJECT + TEXT_TYPE + COMMA_SEP
            + DatabaseSchema.Email.COLUMN_NAME_IS_READ + " BOOLEAN";
    private static final String SQL_CREATE_RECIPIENTS_DATABASE = "";
    private static final String SQL_DELETE_EMAIL_DATABASE = "";
    private static final String SQL_DELETE_RECIPIENTS_DATASE = "";

    public EmailDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_EMAIL_DATABASE);
        db.execSQL(SQL_CREATE_RECIPIENTS_DATABASE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_EMAIL_DATABASE);
        db.execSQL(SQL_DELETE_RECIPIENTS_DATASE);
        onCreate(db);
    }
}
