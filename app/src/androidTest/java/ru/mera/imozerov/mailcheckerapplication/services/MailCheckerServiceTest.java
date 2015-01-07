package ru.mera.imozerov.mailcheckerapplication.services;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.test.ServiceTestCase;

import ru.mera.imozerov.mailcheckerapplication.dto.UserAccount;
import ru.mera.imozerov.mailcheckerapplication.mail.MailHelper;
import ru.mera.imozerov.mailcheckerapplication.sharedPreferences.SharedPreferencesHelper;

/**
 * Created by imozerov on 22.12.2014.
 */
public class MailCheckerServiceTest extends ServiceTestCase<MailCheckerService> {
    public static final UserAccount DUMMY_ACCOUNT = new UserAccount("dummy@gmail.com", "dummy_pass");
    private static MailHelper sMailHelper = new MailHelper(DUMMY_ACCOUNT) {
        @Override
        public boolean isAbleToLogin() {
            return true;
        }
    };


    private MailCheckerService mMailCheckerService;

    public MailCheckerServiceTest(Class serviceClass) {
        super(MailCheckerService.class);
    }

    public MailCheckerServiceTest() {
        super(MailCheckerService.class);
    }


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mMailCheckerService = new MailCheckerService() {
            @Override
            void setMailHelper(MailHelper aMailHelper) {
                this.mMailHelper = sMailHelper;
            }
        };
    }

    public void testPreconditions() {
        assertNotNull(mMailCheckerService);
    }

    public void testStartable() {
        Intent intent = new Intent(MailCheckerService.INTENT_ACTION_NAME);
        startService(intent);
    }

    public void testOnBind() {
        mMailCheckerService.setSharedPreferencesHelper(new SharedPreferencesHelper() {
            @Override
            public boolean hasCredentials(Context aContext) {
                return true;
            }
        });

        Intent intent = new Intent(MailCheckerService.INTENT_ACTION_NAME);
        IBinder binder = mMailCheckerService.onBind(intent);

        assertEquals(MailCheckerService.MailCheckerApiImplementation.class, binder.getClass());
    }

    public void testOnCreate_notLoggedIn() {
        mMailCheckerService.setSharedPreferencesHelper(new SharedPreferencesHelper() {
            @Override
            public boolean hasCredentials(Context aContext) {
                return false;
            }
        });

        mMailCheckerService.onCreate();

        assertNull(mMailCheckerService.getTimer());
    }

    public void testOnCreate_loggedIn() {
        mMailCheckerService.setSharedPreferencesHelper(new SharedPreferencesHelper() {
            @Override
            public boolean hasCredentials(Context aContext) {
                return true;
            }

            @Override
            public UserAccount getUserAccount(Context aContext) {
                return DUMMY_ACCOUNT;
            }
        });

        mMailCheckerService.onCreate();

        assertNotNull(mMailCheckerService.getTimer());
    }
}
