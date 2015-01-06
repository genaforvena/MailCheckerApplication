package ru.mera.imozerov.mailcheckerapplication.mail;

import android.test.InstrumentationTestCase;

import org.mockito.Mockito;

import java.util.Date;
import java.util.List;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Store;
import javax.mail.search.SearchTerm;

import ru.mera.imozerov.mailcheckerapplication.dto.Email;
import ru.mera.imozerov.mailcheckerapplication.dto.UserAccount;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by imozerov on 24.12.2014.
 */
public class MailHelperTest extends InstrumentationTestCase {
    private MailHelper mMailHelper;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        System.setProperty("dexmaker.dexcache", getInstrumentation().getTargetContext().getCacheDir().getPath());

        UserAccount account = mock(UserAccount.class, Mockito.RETURNS_MOCKS);
        final Store store = mock(Store.class);
        Folder inbox = mock(Folder.class);
        Message msg = mock(Message.class, Mockito.RETURNS_MOCKS);
        Multipart multipart = mock(Multipart.class, Mockito.RETURNS_MOCKS);
        Message[] msgs = {msg, msg, msg};

        when(store.getFolder(Mockito.anyString())).thenReturn(inbox);
        when(inbox.search((SearchTerm) any())).thenReturn(msgs);
        when(msg.getContent()).thenReturn(multipart);

        mMailHelper = new MailHelper(account) {
            @Override
            Store getStore() throws MessagingException {
                return store;
            }
        };
    }

    public void testPreconditions() {
        assertNotNull(mMailHelper);
    }

    public void testGetEmailsFromInbox() {
        List<Email> result = mMailHelper.getEmailsFromInbox(new Date(0));
        assertNotNull(result);
        assertEquals(3, result.size());
    }
}
