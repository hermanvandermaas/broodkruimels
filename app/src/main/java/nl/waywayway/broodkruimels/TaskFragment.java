package nl.waywayway.broodkruimels;

import android.app.*;
import android.os.*;
import android.support.v4.app.*;
import android.util.*;
import java.io.*;
import java.util.concurrent.*;
import okhttp3.*;

import android.support.v4.app.Fragment;

/**
 * TaskFragment manages a single background task and retains itself across
 * configuration changes.
 */
public class TaskFragment extends Fragment
{
	/**
	 * Callback interface through which the fragment can report the task's
	 * progress and results back to the Activity.
	 */
	static interface TaskCallbacks
	{
		void onPreExecute();
		void onProgressUpdate(int percent);
		void onCancelled();
		void onPostExecute(String mResult);
	}

	private TaskCallbacks mCallbacks;
	private DummyTask mTask;
	private boolean mRunning;
	private boolean mHasDownloaded;
	private OkHttpClient mClient;
	private String mUrl;
	private Request mRequest;
	private Response mResponse;

	/**
	 * Hold a reference to the parent Activity so we can report the task's current
	 * progress and results. The Android framework will pass us a reference to the
	 * newly created Activity after each configuration change.
	 */
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		if (!(activity instanceof TaskCallbacks))
		{
			throw new IllegalStateException("Activity must implement the TaskCallbacks interface.");
		}

		// Hold a reference to the parent Activity so we can report back the task's
		// current progress and results.
		mCallbacks = (TaskCallbacks) activity;
	}

	/**
	 * This method is called once when the Fragment is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}

	/**
	 * Note that this method is <em>not</em> called when the Fragment is being
	 * retained across Activity instances. It will, however, be called when its
	 * parent Activity is being destroyed for good (such as when the user clicks
	 * the back button, etc.).
	 */
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		cancel();
	}

	/*****************************/
	/***** TASK FRAGMENT API *****/
	/*****************************/

	/**
	 * Start the background task.
	 */
	public void start()
	{
		if (!mRunning)
		{
			mTask = new DummyTask();
			mTask.execute();
			mRunning = true;
		}
	}

	/**
	 * Cancel the background task.
	 */
	public void cancel()
	{
		if (mRunning)
		{
			mTask.cancel(false);
			mTask = null;
			mRunning = false;
		}
	}

	/**
	 * Returns the current state of the background task.
	 */
	public boolean isRunning()
	{
		return mRunning;
	}

	// Getter wel/niet xml gedownload
	public boolean hasDownloaded()
	{
		return mHasDownloaded;
	}	

	// Setter wel/niet xml gedownload
	public void setHasDownloaded(boolean mHasDownloaded)
	{
		this.mHasDownloaded = mHasDownloaded;
	}

	/***************************/
	/***** BACKGROUND TASK *****/
	/***************************/

	/**
	 * A dummy task that performs some (dumb) background work and proxies progress
	 * updates and results back to the Activity.
	 */
	private class DummyTask extends AsyncTask<Void, Integer, String>
	{

		@Override
		protected void onPreExecute()
		{
			// Proxy the call to the Activity.
			mCallbacks.onPreExecute();
			mRunning = true;
		}

		/**
		 * Note that we do NOT call the callback object's methods directly from the
		 * background thread, as this could result in a race condition.
		 */
		@Override
		protected String doInBackground(Void... ignore)
		{
			Log.i("HermLog", "doInBackground" );

			// De asynchrone taak
			OkHttpClient mClient = new OkHttpClient.Builder()
				.readTimeout(30, TimeUnit.SECONDS)
				.build();

			// https://waywayway.nl/bk/?s=0&n=1
			String mUrl = "https://waywayway.nl/bk/?s=0&n=20";
			Request mRequest = new Request.Builder()
				.url(mUrl)
				.build();

			try
			{
				Response mResponse = mClient
					.newCall(mRequest)
					.execute();

				if (!mResponse.isSuccessful())
				{
					throw new IOException("Unexpected code " + mResponse);
				}
				
				Log.i("HermLog", "Gedownload!" );
				return mResponse.body().string();
			}
			catch (IOException e)
			{
				// TODO: catch exception
				Log.i("HermLog", "Exception bij download JSON" );
			}
			
			return "Fout!";

			// Eind asynchrone taak
		}

		@Override
		protected void onProgressUpdate(Integer... percent)
		{
			// Proxy the call to the Activity.
			// mCallbacks.onProgressUpdate(percent[0]);
		}

		@Override
		protected void onCancelled()
		{
			// Proxy the call to the Activity.
			mCallbacks.onCancelled();
			mRunning = false;
		}

		@Override
		protected void onPostExecute(String mResult)
		{
			// Proxy the call to the Activity.
			mCallbacks.onPostExecute(mResult);
			mRunning = false;
		}
	}

	/************************/
	/***** LOGS & STUFF *****/
	/************************/

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onStart()
	{
		super.onStart();
	}

	@Override
	public void onResume()
	{
		super.onResume();
	}

	@Override
	public void onPause()
	{
		super.onPause();
	}

	@Override
	public void onStop()
	{
		super.onStop();
	}
}
