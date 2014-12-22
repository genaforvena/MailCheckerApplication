package ru.mera.imozerov.mailcheckerapplication.services;

import android.test.ServiceTestCase;

/**
 * Created by imozerov on 22.12.2014.
 */
public class MailCheckerServiceTest extends ServiceTestCase {
    private MailCheckerService mMailCheckerService;

    public MailCheckerServiceTest(Class serviceClass) {
        super(serviceClass);
    }

    public MailCheckerServiceTest() {
        super(MailCheckerService.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mMailCheckerService = new MailCheckerService();
    }

    public void testOnCreate_timerIsOn() {
        mMailCheckerService.onCreate();
        assertNotNull(mMailCheckerService.getTimer());
    }
}
