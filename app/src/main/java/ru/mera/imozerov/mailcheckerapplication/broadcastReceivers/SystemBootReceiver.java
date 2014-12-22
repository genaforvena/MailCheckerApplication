package ru.mera.imozerov.mailcheckerapplication.broadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import ru.mera.imozerov.mailcheckerapplication.BuildConfig;
import ru.mera.imozerov.mailcheckerapplication.services.MailCheckerService;

public class SystemBootReceiver extends BroadcastReceiver {
    private static final String TAG = SystemBootReceiver.class.getName();

    public SystemBootReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (BuildConfig.DEBUG) {
            Log.v(TAG, "Boot recieved. Starting service");
        }
        Intent myIntent = new Intent(MailCheckerService.class.getName());
        context.startService(myIntent);
    }
}
