package ru.mera.imozerov.mailcheckerapplication.activities;

import android.content.Context;
import android.content.Intent;
import android.test.ActivityUnitTestCase;
import android.widget.ListView;

import ru.mera.imozerov.mailcheckerapplication.R;
import ru.mera.imozerov.mailcheckerapplication.services.MailCheckerServiceTest;
import ru.mera.imozerov.mailcheckerapplication.sharedPreferences.SharedPreferencesHelper;

/**
 * Created by imozerov on 04.01.2015.
 */
public class EmailListActivityTest extends ActivityUnitTestCase<EmailListActivity> {
    private EmailListActivity mActivity;
    private ListView mEmailListView;
    private Context mContext;

    public EmailListActivityTest() {
        super(EmailListActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        mContext = getInstrumentation().getTargetContext();
        startActivity(new Intent(mContext, EmailListActivity.class), null, null);

        mActivity = getActivity();
        new SharedPreferencesHelper().saveUserAccount(mActivity, MailCheckerServiceTest.DUMMY_ACCOUNT);

        mEmailListView = (ListView) mActivity.findViewById(R.id.email_list_view);
    }

    public void testPreconditions() {
        assertNotNull(mEmailListView);
    }

    public void testWithUserAcc_showList() {
        assertNull(getStartedActivityIntent());
    }

    public void testWithoutUserAcc_goToLoginActivity() {
        new SharedPreferencesHelper().removeUserAccount(mContext);
        getInstrumentation().callActivityOnCreate(mActivity, null);
        Intent startedActivityIntent = getStartedActivityIntent();
        assertNotNull(startedActivityIntent);
    }
}
