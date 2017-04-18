package nl.waywayway.broodkruimels;

import android.app.*;
import android.content.*;
import android.media.*;
import android.net.*;
import android.support.v7.app.*;

// Class voor tonen van notification, na ontvangen en verwerken
// van de intent die wordt verzonden na het afgaan van 
// dagelijks alarm, als dat is ingesteld

public class AlarmReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
	{
        long when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager) context
			.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
			context, 
			0, 
			notificationIntent, 
			PendingIntent.FLAG_UPDATE_CURRENT);

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(context);
		mNotifyBuilder
			.setSmallIcon(R.drawable.ic_stat_bible)
			.setContentTitle("Alarm Fired")
			.setContentText("Events To be Performed")
			.setSound(alarmSound)
			.setAutoCancel(true)
			.setWhen(when)
			.setContentIntent(pendingIntent);
			
        notificationManager.notify(1, mNotifyBuilder.build());
    }
}

