package nl.waywayway.broodkruimels;

import android.app.*;
import android.content.*;
import android.util.*;
import java.util.*;

// Class voor dagelijkse melding op ingestelde tijd

public class MyAlarm
{
	private AlarmManager mAlarmMgr;
	private PendingIntent mAlarmIntent;
	private Context mContext;

	// Constructor
	public MyAlarm(Context context)
	{
		mContext = context;
		mAlarmMgr = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(mContext, AlarmReceiver.class);
		mAlarmIntent = PendingIntent.getBroadcast(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
	}

	// Stel alarm in
	public void setAlarm(int minutesAfterMidnight)
	{
		Log.i("HermLog", "MyAlarm.setAlarm()");
		
		int hours = minutesAfterMidnight / 60;
		int minutes = minutesAfterMidnight % 60;

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.set(Calendar.HOUR_OF_DAY, hours);
		calendar.set(Calendar.MINUTE, minutes);

		// Als het ingestelde tijdstip vandaag al is verstreken
		// op de huidige dag moet het alarm morgen voor het eerst
		// afgaan (anders gaat het direct af)
		if (calendar.getTime().compareTo(new Date()) < 0)
			calendar.add(Calendar.DAY_OF_MONTH, 1);

		// Interval voor alarm, in milliseconden,
		// alarm gaat om de 24 uur af
		int intervalInMillis = 1000 * 60 * 60 * 24;
			
		mAlarmMgr.setRepeating(
			AlarmManager.RTC_WAKEUP, 
			calendar.getTimeInMillis(), 
			intervalInMillis, 
			mAlarmIntent
		);
	}

	// Annuleer alarm
	public void cancelAlarm()
	{
		Log.i("HermLog", "MyAlarm.cancelAlarm()");
		
		if (mAlarmMgr != null)
		{
			mAlarmMgr.cancel(mAlarmIntent);
		}
	}
}
