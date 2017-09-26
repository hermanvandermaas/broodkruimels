package nl.waywayway.broodkruimels;

import com.firebase.jobdispatcher.*;

public class MyJobService extends JobService
{
	@Override
    public boolean onStartJob(JobParameters job)
	{
        // Do some work here

        return false; // Answers the question: "Is there still work going on?"
    }

    @Override
    public boolean onStopJob(JobParameters job)
	{
        return false; // Answers the question: "Should this job be retried?"
    }
}
