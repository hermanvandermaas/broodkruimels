package nl.waywayway.broodkruimels;

import android.app.*;
import android.content.*;
import android.media.*;
import android.net.*;
import android.support.v7.app.*;
import android.util.*;
import com.google.firebase.messaging.*;
import java.util.*;

public class MyFirebaseMessagingService extends FirebaseMessagingService
{
	private Boolean hasData;
	private Boolean hasNotification;
	private Boolean hasUrl;
	private String url;
	
	// Called when message is received.
	// De push notification moet zowel een 'notification'
	// als een 'data' deel bevatten.
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage)
	{
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0)
		{
			Log.i("HermLog", "onMessageReceived(): bevat data");
			hasData = true;
			
			Map<String, String> map = remoteMessage.getData();
			
			// Haal data uit het 'data' gedeelte van de remoteMessage
            for (Map.Entry<String, String> entry : map.entrySet())
			{
                String key = entry.getKey();
				String value = entry.getValue();
				
                Log.i("HermLog", "Key: " + key + ", Value: " + value);

				// In het 'data' deel van de remoteMessage moet een sleutel "url" staan
				// zo ja, dan wordt de Activity 'DetailActivity' opgestart
				if (key.equalsIgnoreCase("url"))
				{
					url = value;
					Log.i("HermLog", "onMessageReceived() url: " + value);
					hasUrl = true;
				}
            }
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null)
		{
			Log.i("HermLog", "onMessageReceived(): bevat notification: getSound(): " + remoteMessage.getNotification().getSound());
			hasNotification = true;
        }
		
		if (hasUrl && hasNotification) 
			sendNotification(
				remoteMessage.getNotification().getTitle(),
				remoteMessage.getNotification().getBody(),
				remoteMessage.getNotification().getSound(),
				url
			);
    }
	
	// Create and show a simple notification containing the received FCM message.
    private void sendNotification(String messageTitle, String messageBody, String messageSound, String messageUrl)
	{
        Intent intent = new Intent(this, DetailActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("url", url);
		
		PendingIntent pendingIntent = 
			TaskStackBuilder.create(this)
			.addNextIntentWithParentStack(intent)
			.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		long when = System.currentTimeMillis();
		
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
		notificationBuilder
			.setSmallIcon(R.drawable.ic_stat_bible)
			.setContentTitle(messageTitle)
			.setContentText(messageBody)
			.setAutoCancel(true)
			.setWhen(when)
			.setContentIntent(pendingIntent);

			if (messageSound != null) notificationBuilder.setSound(defaultSoundUri);
			
        NotificationManager notificationManager =
			(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }
}
