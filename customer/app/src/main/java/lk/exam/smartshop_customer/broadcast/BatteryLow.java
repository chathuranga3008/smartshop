package lk.exam.smartshop_customer.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;


public class BatteryLow extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context.getApplicationContext(), "Please connect your charger.", Toast.LENGTH_LONG).show();
    }

}
