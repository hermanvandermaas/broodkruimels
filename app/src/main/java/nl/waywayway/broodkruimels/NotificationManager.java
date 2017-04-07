package nl.waywayway.broodkruimels;

import android.app.*;
import android.content.*;
import java.util.*;

public class NotificationManager
{
	private AlarmManager mAlarmMgr;
	private PendingIntent mAlarmIntent;
	private Context mContext;

	public NotificationManager(Context context)
	{
		this.mContext = context;
	}

	// Stel alarm in
	private void setAlarm(int minutesAfterMidnight)
	{
		context = 
		
		alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, AlarmReceiver.class);
		alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.set(Calendar.HOUR_OF_DAY, 8);
		calendar.set(Calendar.MINUTE, 30);

		alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
							  1000 * 60 * 20, alarmIntent);
	}

}
