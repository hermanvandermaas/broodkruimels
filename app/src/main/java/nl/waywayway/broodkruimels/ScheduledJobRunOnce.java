package nl.waywayway.broodkruimels;

import android.os.*;
import com.firebase.jobdispatcher.*;
import nl.waywayway.broodkruimels.*;

public class ScheduledJobRunOnce
{
	private FirebaseJobDispatcher dispatcher;
	public static final String TAG_SCHEDULED_JOB_RUN_ONCE = "tag_scheduled_job_run_once";

	public ScheduledJobRunOnce(FirebaseJobDispatcher dispatcher)
	{
		this.dispatcher = dispatcher;
	}

	public void schedule()
	{
		/*
		 Bundle myExtrasBundle = new Bundle();
		 myExtrasBundle.putString("some_key", "some_value");
		 */

		RetryStrategy retryStrategy = dispatcher.newRetryStrategy(RetryStrategy.RETRY_POLICY_EXPONENTIAL, 20, 120);
		 
		Job myJob = dispatcher.newJobBuilder()
			// the JobService that will be called
			.setService(MyJobService.class)
			// uniquely identifies the job
			.setTag(TAG_SCHEDULED_JOB_RUN_ONCE)
			// one-off job
			.setRecurring(false)
			// don't persist past a device reboot
			.setLifetime(Lifetime.UNTIL_NEXT_BOOT)
			// start between x and x seconds from now
			.setTrigger(Trigger.executionWindow(0, 20))
			// overwrite an existing job with the same tag
			.setReplaceCurrent(true)
			// retry with exponential backoff
			.setRetryStrategy(retryStrategy)
			// constraints that need to be satisfied for the job to run
			.setConstraints(
				// only run on an unmetered network
				Constraint.ON_ANY_NETWORK
			)
			.build();

		dispatcher.mustSchedule(myJob);
	}
}
