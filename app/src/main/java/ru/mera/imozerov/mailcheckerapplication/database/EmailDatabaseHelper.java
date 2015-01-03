package ru.mera.imozerov.mailcheckerapplication.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by imozerov on 29.12.2014.
 */
public class EmailDatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = EmailDatabaseHelper.class.getName();

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "MailChecker.db";
    public static final String INTEGER_TYPE = " INTEGER";
    public static final String TEXT_TYPE = " TEXT";
    public static final String COMMA_SEP = ",";

    public EmailDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "Creating dbs");
        EmailTable.onCreate(db);
        RecipientTable.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "Upgrading db from " + oldVersion + " to " + newVersion);
        EmailTable.onUpgrade(db, oldVersion, newVersion);
        RecipientTable.onUpgrade(db, oldVersion, newVersion);
    }
}
