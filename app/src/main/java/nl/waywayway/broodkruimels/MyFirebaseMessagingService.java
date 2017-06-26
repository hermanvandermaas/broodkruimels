package nl.waywayway.broodkruimels;

import android.app.*;
import android.content.*;
import android.media.*;
import android.net.*;
import android.support.v7.app.*;
import android.util.*;
import com.google.firebase.messaging.*;

public class MyFirebaseMessagingService extends FirebaseMessagingService
{
	private Boolean hasData = false;
	private Boolean hasNotification = false;
	
	// Called when message is received.
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage)
	{
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0)
		{
			Log.i("HermLog", "onMessageReceived(): data: " + remoteMessage.getData());
			hasData = true;
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null)
		{
			Log.i("HermLog", "onMessageReceived(): notification: " + remoteMessage.getNotification().getBody());
			hasNotification = true;
        }
		
		if (hasData && hasNotification) sendNotification(remoteMessage.getNotification().getBody());
    }
	
	// Create and show a simple notification containing the received FCM message.
    private void sendNotification(String messageBody)
	{
        Intent intent = new Intent(this, DetailActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		
		PendingIntent pendingIntent = 
			TaskStackBuilder.create(this)
			.addNextIntentWithParentStack(intent)
			.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		long when = System.currentTimeMillis();
		String title = this.getResources().getString(R.string.notification_title);
		
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
		notificationBuilder
			.setSmallIcon(R.drawable.ic_stat_bible)
			.setContentTitle(title)
			.setContentText(messageBody)
			.setAutoCancel(true)
			.setSound(defaultSoundUri)
			.setWhen(when)
			.setContentIntent(pendingIntent);

        NotificationManager notificationManager =
			(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }
}
