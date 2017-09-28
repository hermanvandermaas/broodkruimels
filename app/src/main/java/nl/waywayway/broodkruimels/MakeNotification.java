package nl.waywayway.broodkruimels;

import android.app.*;
import android.content.*;
import android.media.*;
import android.net.*;
import android.support.v4.app.*;
import com.google.firebase.iid.*;

import android.support.v4.app.TaskStackBuilder;

public class MakeNotification
{
	private Context context;
	
	public MakeNotification(Context context)
	{
		this.context = context;
	}

	public void showNotification()
	{
		long when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager) context
			.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notificationIntent = new Intent(context,  DetailActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		PendingIntent pendingIntent = 
			TaskStackBuilder.create(context)
			.addNextIntentWithParentStack(notificationIntent)
			.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

		String notificationTitle = context.getResources().getString(R.string.notification_title);
		String notificationText = context.getResources().getString(R.string.notification_text);

        NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(context);
		mNotifyBuilder
			.setSmallIcon(R.drawable.ic_stat_bible)
			.setContentTitle(notificationTitle)
			.setContentText(notificationText)
			.setSound(alarmSound)
			.setAutoCancel(true)
			.setWhen(when)
			.setContentIntent(pendingIntent);

        notificationManager.notify(1, mNotifyBuilder.build());
	}
}
