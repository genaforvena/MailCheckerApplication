package ru.mera.imozerov.mailcheckerapplication.mail;

import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Store;

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

    public List<Email> getEmailsFromInbox() {
        List<Email> emailListResult = new ArrayList<Email>();

        try {
            Store store = getStore();
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);
            Message[] msgs = inbox.getMessages();

            for (Message msg : msgs) {
                Email email = new Email();

                List<Address> in = Arrays.asList(msg.getFrom());
                email.setSenders(in);

                Multipart mp = (Multipart) msg.getContent();
                BodyPart bp = mp.getBodyPart(0);
                email.setSentDate(msg.getSentDate());
                email.setSubject(msg.getSubject());

                // TODO get actual content of message
                email.setText(bp.getContent().toString());

                emailListResult.add(email);
            }
        } catch (MessagingException e) {
            Log.e(TAG, "Unable to get incoming messages!", e);
        } catch (IOException e) {
            Log.e(TAG, "Unable to get content of a message!", e);
        }

        return emailListResult;
    }

    Store getStore() throws MessagingException {
        Session session = Session.getInstance(mProps, null);
        Store store = session.getStore();
        store.connect("imap.gmail.com", mUserAccount.getLogin(), mUserAccount.getPassword());
        return store;
    }
}
