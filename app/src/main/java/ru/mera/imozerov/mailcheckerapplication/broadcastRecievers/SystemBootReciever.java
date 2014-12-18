package ru.mera.imozerov.mailcheckerapplication.broadcastRecievers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import ru.mera.imozerov.mailcheckerapplication.BuildConfig;
import ru.mera.imozerov.mailcheckerapplication.services.MailCheckerService;

public class SystemBootReciever extends BroadcastReceiver {
    private static final String TAG = SystemBootReciever.class.getName();

    public SystemBootReciever() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (BuildConfig.DEBUG) {
            Log.v(TAG, "Boot recieved. Starting service");
        }
        Intent myIntent = new Intent(context, MailCheckerService.class);
        context.startService(myIntent);
    }
}
