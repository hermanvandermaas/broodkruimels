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
	private OkHttpClient mClient;
	private String mUrl;
	private Request mRequest;
	private Response mResponse;
	private int startItem;
	private int itemsPerPage;
	private int feedsListSize;
	String url;

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

		// itemsperpage is aantal items dat per keer opgehaald moet worden
		itemsPerPage = getActivity().getResources().getInteger(R.integer.items_per_page);

		// url is de basis url voor ophalen data
		url = getActivity().getResources().getString(R.string.url_data);
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
	// parameter false: eerste download
	// parameter true: extra data downoaden bij endless scrolling
	public void start(Boolean downloadMoreItems)
	{
		if (!mRunning)
		{
			mTask = new DummyTask();
			mTask.execute(downloadMoreItems);
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

	// setter en getter voor feedsListSize, gebruikt voor
	// bepalen welke extra data te downloaden bij endless scrolling
	public void setFeedsListSize(int feedsListSize)
	{
		this.feedsListSize = feedsListSize;
	}

	public int getFeedsListSize()
	{
		return feedsListSize;
	}

	/**
	 * A dummy task that performs some (dumb) background work and proxies progress
	 * updates and results back to the Activity.
	 */
	private class DummyTask extends AsyncTask<Boolean, Integer, String>
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
		protected String doInBackground(Boolean... downloadMoreItems)
		{
			Log.i("HermLog", "doInBackground");
			Log.i("HermLog", "downloadMoreItems[0]: " + downloadMoreItems[0].toString() );

			// De asynchrone taak
			OkHttpClient mClient = new OkHttpClient.Builder()
				.readTimeout(30, TimeUnit.SECONDS)
				.build();

			/** Maak URL voor downloaden data
			 Query string heeft de vorm:
			 ?s=0&n=40
			 waarin:
			 s=eerste op te halen item in de gesorteerde lijst met alle items,
			 let op: het eerste item is item 0
			 n=aantal op te halen items binnen de lijst met alle items, inclusief item nummer "s"

			 Endless scrolling:
			 als er al eerder gedownloade data in de List<E> staan, begin nieuwe download bij eerstvolgende item
			 */
			 
			if (downloadMoreItems[0])
			{
				// In geval eerste download
				startItem = 0;
			}
			else
			{
				// In geval latere downloads voor endless scrolling
				startItem = getFeedsListSize();
			}

			Log.i("HermLog", "startItem: " + startItem);

			String mUrl = url
				+ "s="
				+ Integer.toString(startItem)
				+ "&n="
				+ Integer.toString(itemsPerPage);

			Log.i("HermLog", "mUrl: " + mUrl);

			Request mRequest = new Request.Builder()
				.url(mUrl)
				.build();

			try
			{
				Response mResponse = mClient
					.newCall(mRequest)
					.execute();

				if ( !mResponse.isSuccessful() )
				{
					throw new IOException("Unexpected code " + mResponse);
				}

				Log.i("HermLog", "Gedownload!");

				return mResponse.body().string();
			}
			catch (IOException e)
			{
				// TODO: catch exception
				Log.i("HermLog", "Exception bij download JSON");
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
