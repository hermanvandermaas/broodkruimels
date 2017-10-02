package nl.waywayway.broodkruimels;

import android.content.*;
import android.os.*;
import android.util.*;
import com.firebase.jobdispatcher.*;

public class MyJobService extends JobService
{
	private AsyncTask mBackgroundTask;

	@Override
    public boolean onStartJob(final JobParameters job)
	{
		mBackgroundTask = new AsyncTask<Object, Void, String>()
		{
			@Override
			protected String doInBackground(Object... mObject)
			{
				Context context = MyJobService.this;

				Log.i("HermLog", "onStartJob");

				return "s";
			}

			@Override
			protected void onPostExecute(String result)
			{
				Log.i("HermLog", "MyJobService: onPostExecute(), result: " + result);
				
				// false: job is done, no rescheduling
				jobFinished(job, false);
				
			}
		};

		mBackgroundTask.execute();

        return true;
		// Answers the question: "Is there still work going on?"
		// ja, in geval er een aparte thread is gestart
    }

    @Override
    public boolean onStopJob(JobParameters job)
	{
		Log.i("HermLog", "onStopJob");
        return false;
		// Answers the question: "Should this job be retried?"
    }
}
