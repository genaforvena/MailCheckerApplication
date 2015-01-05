package ru.mera.imozerov.mailcheckerapplication.mail;

import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeMultipart;
import javax.mail.search.ComparisonTerm;
import javax.mail.search.ReceivedDateTerm;
import javax.mail.search.SearchTerm;

import ru.mera.imozerov.mailcheckerapplication.dto.Email;
import ru.mera.imozerov.mailcheckerapplication.dto.UserAccount;

/**
 * Created by imozerov on 23.12.2014.
 */
public class MailHelper {
    private static final String TAG = MailHelper.class.getName();

    private final UserAccount mUserAccount;
    private final Properties mProps;

    public MailHelper(UserAccount userAccount){
        mProps = new Properties();
        mProps.setProperty("mail.store.protocol", "imaps");

        String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
        mProps.setProperty("mail.pop3.socketFactory.class", SSL_FACTORY);
        mProps.setProperty("mail.pop3.socketFactory.fallback", "false");
        mProps.setProperty("mail.pop3.port", "995");
        mProps.setProperty("mail.pop3.socketFactory.port", "995");

        this.mUserAccount = userAccount;
    }

    public boolean isAbleToLogin() {
        try {
            getStore();
            return true;
        } catch (MessagingException e) {
            Log.w(TAG, "Unable to getStore for user " + mUserAccount, e);
            return false;
        }
    }

    public List<Email> getEmailsFromInbox(Date startDate) {
        List<Email> emailListResult = new ArrayList<>();

        try {
            Store store = getStore();
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);

            SearchTerm newerThan = new ReceivedDateTerm(ComparisonTerm.GT, startDate);
            Message[] msgs = inbox.search(newerThan);

            for (Message msg : msgs) {
                Email email = new Email();

                List<String> senders = new ArrayList<>();
                for (Address sender : msg.getFrom()) {
                    senders.add(sender.toString());
                }
                email.setSenderEmail(senders.get(0));
                email.setSentDate(msg.getSentDate());
                email.setSubject(msg.getSubject());

                String content = getContent(msg);
                email.setContent(content);

                emailListResult.add(email);
            }
        } catch (MessagingException e) {
            Log.e(TAG, "Unable to get incoming messages!", e);
        } catch (IOException e) {
            Log.e(TAG, "Unable to get content of a message!", e);
        }

        return emailListResult;
    }

    private String getContent(Message msg) throws IOException, MessagingException {
        String content = null;
        Object contentObject = msg.getContent();
        if (contentObject instanceof MimeMultipart) {
            MimeMultipart multipart = (MimeMultipart) contentObject;
            for (int j = 0; j < multipart.getCount(); j++) {

                BodyPart bodyPart = multipart.getBodyPart(j);

                String disposition = bodyPart.getDisposition();

                if (disposition != null && (disposition.equalsIgnoreCase("ATTACHMENT"))) {
                    //Skip this is attachment
                } else {
                    content = bodyPart.getContent().toString();
                    break;
                }
            }
        } else {
            content = contentObject.toString();
        }

        if (content == null) {
            content = "Attachment only message";
        }
        return content;
    }

    Store getStore() throws MessagingException {
        Session session = Session.getInstance(mProps, null);
        Store store = session.getStore();
        store.connect("imap.gmail.com", mUserAccount.getEmailAddress(), mUserAccount.getPassword());
        return store;
    }
}
