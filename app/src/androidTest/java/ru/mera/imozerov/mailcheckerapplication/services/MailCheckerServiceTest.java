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
            void setMailHelper(MailHelper mMailHelper) {
                this.mMailHelper = new MailHelper(DUMMY_ACCOUNT) {
                    @Override
                    public boolean isAbleToLogin() {
                        return true;
                    }
                };
            }
        };

    }

    public void testPreconditions() {
        assertNotNull(mMailCheckerService);
    }

    public void testStartable() {
        Intent intent = new Intent(MailCheckerService.class.getName());
        startService(intent);
    }

    public void testOnBind_notLoggedInAndNotAttempting() {
        mMailCheckerService.setSharedPreferencesHelper(new SharedPreferencesHelper() {
            @Override
            public boolean isLoggedIn(Context context) {
                return false;
            }
            @Override
            public void saveUserAccount(Context context, UserAccount account) {}
        });

        Intent intent = new Intent(MailCheckerService.class.getName());
        IBinder binder = mMailCheckerService.onBind(intent);
        assertNull(binder);
    }

    public void testOnBind_loginAttemptSuccess() {
        mMailCheckerService.setSharedPreferencesHelper(new SharedPreferencesHelper() {
            @Override
            public boolean isLoggedIn(Context context) {
                return true;
            }
            @Override
            public void saveUserAccount(Context context, UserAccount account) {}
        });

        Intent intent = new Intent(MailCheckerService.class.getName());
        intent.putExtra(MailCheckerService.LOGIN, "login");
        intent.putExtra(MailCheckerService.PASSWORD, "password");
        IBinder binder = mMailCheckerService.onBind(intent);

        assertEquals(MailCheckerService.MailCheckerApiImplementation.class, binder.getClass());
        assertNotNull(mMailCheckerService.getUpdateTask());
        assertNotNull(mMailCheckerService.getTimer());
    }

    public void testOnStartCommand_notLoggedIn() {
        mMailCheckerService.setSharedPreferencesHelper(new SharedPreferencesHelper() {
            @Override
            public boolean isLoggedIn(Context context) {
                return false;
            }
            @Override
            public void saveUserAccount(Context context, UserAccount account) {}
        });

        Intent intent = new Intent(MailCheckerService.class.getName());
        mMailCheckerService.onStartCommand(intent, 0, 0);

        assertNull(mMailCheckerService.getTimer());
    }

    public void testOnStartCommand_loggedIn() {
        mMailCheckerService.setSharedPreferencesHelper(new SharedPreferencesHelper() {
            @Override
            public boolean isLoggedIn(Context context) {
                return true;
            }
            @Override
            public void saveUserAccount(Context context, UserAccount account) {}
        });

        Intent intent = new Intent(MailCheckerService.class.getName());
        mMailCheckerService.onStartCommand(intent, 0, 0);

        assertNotNull(mMailCheckerService.getTimer());
    }
}
