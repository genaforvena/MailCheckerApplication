package ru.mera.imozerov.mailcheckerapplication.broadcastRecievers;

import android.content.Intent;
import android.test.AndroidTestCase;
import android.test.ServiceTestCase;

/**
 * Created by imozerov on 18.12.2014.
 */
public class SystemBootRecieverTest extends AndroidTestCase {
    private SystemBootReciever mSystemBootReciever;
    private TestContext mContext;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        mSystemBootReciever = new SystemBootReciever();
        mContext = new TestContext();
    }

    public void testOnStart_startService() {
        Intent intent = new Intent(Intent.ACTION_BOOT_COMPLETED);
        mSystemBootReciever.onReceive(mContext, intent);
        assertEquals(1, mContext.getStartedIntents().size());
    }

}
