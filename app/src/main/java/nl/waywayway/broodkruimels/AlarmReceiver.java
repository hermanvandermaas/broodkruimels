package nl.waywayway.broodkruimels;

import android.content.*;
import android.util.*;
import com.firebase.jobdispatcher.*;

// Class voor tonen van notification, na ontvangen en verwerken
// van de intent die wordt verzonden na het afgaan van 
// dagelijks alarm, als dat is ingesteld

public class AlarmReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
	{
		MakeNotification notification = new MakeNotification(context);
		notification.showNotification();
		Log.i("HermLog", "AlarmReveiver: onReceive()");
    }
}

