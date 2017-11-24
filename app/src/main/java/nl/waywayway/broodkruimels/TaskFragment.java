package nl.waywayway.broodkruimels;

import android.content.*;
import android.os.*;
import android.support.v4.app.*;
import android.util.*;
import com.google.gson.reflect.*;
import java.util.*;

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
	public interface TaskCallbacks
	{
		public void onPreExecute();
		public void onProgressUpdate(int percent);
		public void onCancelled();
		public void onPostExecute(String mResult, Boolean downloadMoreItems, ArrayList<FeedItem> feedsList, ArrayList<CategoryItem> categoryList);
	}

	private static final String CLASSNAME_DETAIL_ACTIVITY = "DetailActivity";
	private Context context;
	private TaskCallbacks mCallbacks;
	private DummyTask mTask;
	private boolean mRunning;
	private int startItem;
	private int itemsPerPage;
	private int feedsListSize;
	private ArrayList<FeedItem> feedsList;
	private ArrayList<CategoryItem> categoryList;
	private String categoriesParameter;
	private Boolean getExtraPage;
	
	/**
	 * Hold a reference to the parent Activity so we can report the task's current
	 * progress and results. The Android framework will pass us a reference to the
	 * newly created Activity after each configuration change.
	 */
	@Override
	public void onAttach(Context context)
	{
		super.onAttach(context);
		this.context = context;

		if (!(context instanceof TaskCallbacks))
		{
			throw new IllegalStateException("Activity must implement the TaskCallbacks interface.");
		}

		// Hold a reference to the parent Activity so we can report back the task's
		// current progress and results.
		mCallbacks = (TaskCallbacks) context;

		// itemsperpage is aantal items dat per keer opgehaald moet worden
		// als deze taskfragment aan DetailActivity wordt verbonden,
		// alleen een enkel, meest recente, item downloaden
		String attachedClassName = getActivity().getClass().getSimpleName();
		Log.i("HermLog", "Class naam: " + attachedClassName);
		itemsPerPage = getActivity().getResources().getInteger(R.integer.items_per_page);
		if (attachedClassName.equals(CLASSNAME_DETAIL_ACTIVITY))
			itemsPerPage = 1;
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
	public void start(Boolean downloadMoreItems, ArrayList<FeedItem> feedsList, ArrayList<CategoryItem> categoryList)
	{
		if (!mRunning)
		{
			mTask = new DummyTask(feedsList, categoryList);
			mTask.execute(downloadMoreItems);
			mRunning = true;
			// geeft weer of dit eerste download is of 
			// extra data voor endless scrolling
			getExtraPage = downloadMoreItems;
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
			getExtraPage = false;
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

	// Setter voor URL query string parameter voor te
	// downloaden categorieen
	public void setCategoriesParameter(String categoriesParameter)
	{
		if (categoriesParameter != null && !categoriesParameter.trim().isEmpty())
			this.categoriesParameter = "&c=" + categoriesParameter;
		else
			this.categoriesParameter = "";

		Log.i("HermLog", "categoriesParameter: " + categoriesParameter);
	}

	// Setter en getter voor getExtraPage, gebruikt voor bepalen
	// of taskfragment gebruikt wordt voor eerste download data of extra 'page' met data
	// voor endless scrolling
	public void setGetExtraPage(Boolean getExtraPage)
	{
		this.getExtraPage = getExtraPage;
	}

	public Boolean getGetExtraPage()
	{
		return getExtraPage;
	}

	/**
	 * A dummy task that performs some (dumb) background work and proxies progress
	 * updates and results back to the Activity.
	 */
	private class DummyTask extends AsyncTask<Boolean, Integer, String>
	{
		private ArrayList<FeedItem> feedsList;
		private ArrayList<CategoryItem> categoryList;
		
		public DummyTask(ArrayList<FeedItem> feedsList, ArrayList<CategoryItem> categoryList)
		{
			this.feedsList = feedsList;
			this.categoryList = categoryList;
		}

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
			Log.i("HermLog", "downloadMoreItems[0]: " + downloadMoreItems[0].toString());

			if (!downloadMoreItems[0])
			{
				// In geval eerste download
				startItem = 0;
				Log.i("HermLog", "eerste download");
			}
			else
			{
				// In geval latere downloads voor endless scrolling
				startItem = getFeedsListSize();
				Log.i("HermLog", "latere download");
			}

			Log.i("HermLog", "startItem: " + startItem);

			// maak url en download data json string
			String baseUrl = getActivity().getResources().getString(R.string.url_data);
			MakeUrl mUrlMaker = new MakeUrl(baseUrl, startItem, itemsPerPage, categoriesParameter);
			DownloadJsonString mDownloader = new DownloadJsonString(mUrlMaker.make());

			return mDownloader.download();
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
			getExtraPage = false;
		}

		@Override
		protected void onPostExecute(String mResult)
		{	
			parseResult(mResult);
		
			// Proxy the call to the Activity
			mCallbacks.onPostExecute(mResult, getExtraPage, feedsList, categoryList);

			mRunning = false;
			getExtraPage = false;
		}
	}
	
	// json string verwerken na download
	// Zet json string per item in ArrayList<T>
	private void parseResult(String result)
	{
		Log.i("HermLog", "parseResult()");
		feedsList = JsonToArrayListParser.getJsonToArrayListParser().parse(result, context.getResources().getString(R.string.json_items_list_root_element), feedsList, new TypeToken<ArrayList<FeedItem>>(){}.getType());

		if (categoryList.size() > 0) return;
		
		categoryList = JsonToArrayListParser.getJsonToArrayListParser().parse(result, context.getResources().getString(R.string.json_categories_list_root_element), categoryList, new TypeToken<ArrayList<CategoryItem>>(){}.getType());
		Log.i("HermLog", "categoryList size: " + categoryList.size());
	}
}
