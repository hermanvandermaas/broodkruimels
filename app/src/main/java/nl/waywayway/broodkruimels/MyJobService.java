package nl.waywayway.broodkruimels;

import android.content.*;
import android.os.*;
import android.util.*;
import com.firebase.jobdispatcher.*;

public class MyJobService extends JobService
{
	private AsyncTask mBackgroundTask;

	@Override
    public boolean onStartJob(JobParameters job)
	{
		final JobParameters jobParameters = job;

        // Do some work here
		mBackgroundTask = new AsyncTask<Void, Void, String>()
		{
			@Override
			protected String doInBackground(Void... mVoid)
			{
				Context context = MyJobService.this;

				Log.i("HermLog", "onStartJob");

				return "s";
			}

			@Override
			protected void onPostExecute(String result)
			{
				// false: job is done, no rescheduling
				jobFinished(jobParameters, false);
				Log.i("HermLog", "onStartJob: OnPostExecute");
			}
		};

		mBackgroundTask.execute();

        return false;
		// Answers the question: "Is there still work going on?"
    }

    @Override
    public boolean onStopJob(JobParameters job)
	{
        return false;
		// Answers the question: "Should this job be retried?"
    }
}
