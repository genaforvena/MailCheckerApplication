package ru.mera.imozerov.mailcheckerapplication.services;

import android.content.Intent;
import android.os.IBinder;
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

    public void testOnBind_returnsNull_ifNotLoggedInAndNotAttempting() {
        Intent intent = new Intent(MailCheckerService.class.getName());
        IBinder binder = mMailCheckerService.onBind(intent);
        assertNull(binder);
    }

    public void testOnBind_returnsIBinder_ifLoginAttempt() {
        Intent intent = new Intent(MailCheckerService.class.getName());
        intent.putExtra(MailCheckerService.LOGIN, "login");
        intent.putExtra(MailCheckerService.PASSWORD, "password");
        IBinder binder = mMailCheckerService.onBind(intent);
        assertEquals(MailCheckerService.MailCheckerApiImplementation.class, binder.getClass());
    }
}
