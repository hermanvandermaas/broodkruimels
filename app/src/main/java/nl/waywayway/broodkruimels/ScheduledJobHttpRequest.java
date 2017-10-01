package nl.waywayway.broodkruimels;

import android.os.*;
import com.firebase.jobdispatcher.*;
import nl.waywayway.broodkruimels.*;

public class ScheduledJobHttpRequest
{
	private FirebaseJobDispatcher dispatcher;
	public static final String TAG_NOTIFICATION_SCHEDULED_JOB = "tag_daily_notification_scheduled_job";

	public ScheduledJobHttpRequest(FirebaseJobDispatcher dispatcher)
	{
		this.dispatcher = dispatcher;
	}

	public void schedule()
	{
		/*
		 Bundle myExtrasBundle = new Bundle();
		 myExtrasBundle.putString("some_key", "some_value");
		 */

		Job myJob = dispatcher.newJobBuilder()
			// the JobService that will be called
			.setService(MyJobService.class)
			// uniquely identifies the job
			.setTag(TAG_NOTIFICATION_SCHEDULED_JOB)
			// one-off job
			.setRecurring(false)
			// don't persist past a device reboot
			.setLifetime(Lifetime.UNTIL_NEXT_BOOT)
			// start between x and x seconds from now
			.setTrigger(Trigger.executionWindow(0, 1))
			// overwrite an existing job with the same tag
			.setReplaceCurrent(true)
			// retry with exponential backoff
			.setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
			// constraints that need to be satisfied for the job to run
			/*
			.setConstraints(
			// only run on an unmetered network
			Constraint.ON_UNMETERED_NETWORK,
			// only run when the device is charging
			Constraint.DEVICE_CHARGING)
			// .setExtras(myExtrasBundle)
			*/
			.build();

		dispatcher.mustSchedule(myJob);
	}
}
