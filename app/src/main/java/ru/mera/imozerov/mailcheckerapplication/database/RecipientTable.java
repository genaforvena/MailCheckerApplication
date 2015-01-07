package ru.mera.imozerov.mailcheckerapplication.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by imozerov on 03.01.2015.
 */
public final class RecipientTable {
    private static final String TAG = RecipientTable.class.getName();

    public static final String TABLE_NAME = "recipients";
    public static final String COLUMN_NAME_RECIPENT_ID = "_id";
    public static final String COLUMN_NAME_EMAIL_ID = "email_id";
    public static final String COLUMN_NAME_RECIPIENT_EMAIL = "recipient_email";

    public static final String[] allColumns = {COLUMN_NAME_RECIPENT_ID, COLUMN_NAME_RECIPIENT_EMAIL, COLUMN_NAME_EMAIL_ID};

    private static final String SQL_CREATE_RECIPIENTS_DATABASE = "CREATE TABLE " + TABLE_NAME
            + " (" + COLUMN_NAME_RECIPENT_ID + EmailDatabaseHelper.INTEGER_TYPE + " PRIMARY KEY AUTOINCREMENT" + EmailDatabaseHelper.COMMA_SEP
            + COLUMN_NAME_EMAIL_ID + EmailDatabaseHelper.INTEGER_TYPE + EmailDatabaseHelper.COMMA_SEP
            + COLUMN_NAME_RECIPIENT_EMAIL + EmailDatabaseHelper.TEXT_TYPE + EmailDatabaseHelper.COMMA_SEP
            + "FOREIGN KEY(" + COLUMN_NAME_EMAIL_ID + ") REFERENCES " + EmailTable.TABLE_NAME +
            "(" + EmailTable.COLUMN_NAME_EMAIL_ID + ") ON DELETE CASCADE" + ")";

    private static final String SQL_DELETE_RECIPIENTS_DATABASE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public static void onCreate(SQLiteDatabase aSQLiteDatabase) {
            aSQLiteDatabase.execSQL(SQL_CREATE_RECIPIENTS_DATABASE);
    }

    public static void onUpgrade(SQLiteDatabase aSQLiteDatabase, int aOldVersion, int aNewVersion) {
        Log.i(TAG, "Upgrading db from " + aOldVersion + " to " + aNewVersion);
        recreateDb(aSQLiteDatabase);
    }

    public static void recreateDb(SQLiteDatabase aSQLiteDatabase) {
        aSQLiteDatabase.execSQL(SQL_DELETE_RECIPIENTS_DATABASE);
        onCreate(aSQLiteDatabase);
    }
}
