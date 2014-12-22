package ru.mera.imozerov.mailcheckerapplication.broadcastRecievers;

import android.content.ComponentName;
import android.content.Intent;
import android.test.mock.MockContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by imozerov on 18.12.2014.
 */
public class TestContext extends MockContext
{
    private List<Intent> mStartedIntents = new ArrayList<Intent>();

    @Override
    public ComponentName startService(Intent xiIntent)
    {
        mStartedIntents.add(xiIntent);
        return null;
    }

    public List<Intent> getStartedIntents()
    {
        return mStartedIntents;
    }

    @Override
    public String getPackageName() {
        return TestContext.class.getPackage().getName();
    }
}
