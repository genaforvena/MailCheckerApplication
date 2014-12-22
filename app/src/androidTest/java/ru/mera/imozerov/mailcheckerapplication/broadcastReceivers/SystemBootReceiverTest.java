package ru.mera.imozerov.mailcheckerapplication.broadcastReceivers;

import android.content.Intent;
import android.test.AndroidTestCase;

import ru.mera.imozerov.mailcheckerapplication.services.MailCheckerService;

/**
 * Created by imozerov on 18.12.2014.
 */
public class SystemBootReceiverTest extends AndroidTestCase {
    private SystemBootReceiver mSystemBootReceiver;
    private TestContext mContext;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        mSystemBootReceiver = new SystemBootReceiver();
        mContext = new TestContext();
    }

    public void testOnStart_startService() {
        Intent intent = new Intent(Intent.ACTION_BOOT_COMPLETED);
        mSystemBootReceiver.onReceive(mContext, intent);
        assertEquals(1, mContext.getStartedIntents().size());
        assertEquals(MailCheckerService.class.getName(), mContext.getStartedIntents().get(0).getAction());
    }

}
