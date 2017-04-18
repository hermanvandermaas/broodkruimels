package nl.waywayway.broodkruimels;

import android.content.*;
import android.support.v7.preference.*;
import android.util.*;

public class BootReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
	{
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED"))
		{
			// Lees ingestelde tijd, als geen ingestelde tijd is gevonden, neem default
			SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
			int prefTimeDefault = context.getResources().getInteger(R.integer.preferences_time_default);
			int minutesAfterMidnight = sharedPref.getInt(SettingsActivity.KEY_PREF_NOTIFY_TIME, prefTimeDefault);

			Log.i("HermLog", "BootReceiver.onReceive(): minutesAfterMidnight: " + minutesAfterMidnight);

			// Stel alarm in
			MyAlarm mAlarm = new MyAlarm(context);
			mAlarm.setAlarm(minutesAfterMidnight);
        }
    }
}
