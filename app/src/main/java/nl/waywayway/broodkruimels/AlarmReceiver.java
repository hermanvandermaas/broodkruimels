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
		// Firebase Job Dispatcher library
		// Create a new dispatcher using the Google Play driver.
		FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));
		ScheduledJobRunOnce job = new ScheduledJobRunOnce(dispatcher);
		job.schedule();
		Log.i("HermLog", "onReceive(): job scheduled");
    }
}

