package ru.mera.imozerov.mailcheckerapplication.database;

/**
 * Created by imozerov on 26.12.2014.
 */
public final class DatabaseSchema {
    public final class Email {
        public static final String TABLE_NAME = "emails";
        public static final String COLUMN_NAME_EMAIL_ID = "_id";
        public static final String COLUMN_NAME_SENDER_EMAIL = "sender_email";
        public static final String COLUMN_NAME_SENT_DATE = "sent";
        public static final String COLUMN_NAME_SUBJECT = "subject";
        public static final String COLUMN_NAME_CONTENT = "content";
        public static final String COLUMN_NAME_IS_READ = "is_read";
    }

    public final class Recipients {
        public static final String TABLE_NAME = "recipients";
        public static final String COLUMN_NAME_RECIPENT_ID = "_id";
        public static final String COLUMN_NAME_EMAIL_ID = "email_id";
        public static final String COLUMN_NAME_RECIPIENT_EMAIL = "recipient_email";
    }
}
