package nl.waywayway.broodkruimels;

import android.content.*;
import android.net.*;
import android.os.*;
import android.support.design.widget.*;
import android.support.v4.app.*;
import android.support.v7.app.*;
import android.support.v7.widget.*;
import android.text.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import java.text.*;
import java.util.*;
import org.json.*;

import android.support.v7.widget.Toolbar;

public class MainActivity extends AppCompatActivity implements TaskFragment.TaskCallbacks
{
	private static final String TAG_TASK_FRAGMENT = "task_fragment";
	private static final String TAG_SCROLLPOSITION = "scroll_position";
	private ActionBar actionBar;
	private TaskFragment mTaskFragment;
    private List<FeedItem> feedsList;
    private RecyclerView mRecyclerView;
	private LinearLayoutManager mLinearLayoutManager;
	private Parcelable mScrollposition;
    private MyRecyclerViewAdapter adapter;
	private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Log.i("HermLog", "onCreate()" );
		
		// zet referentie naar context van deze activity in een variabele
		mContext = this;

		// Maak toolbar
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		ActionBar actionBar = getSupportActionBar();

		// Klik knop probeer opnieuw:
		// check verbinding, indien ok dan json laden
		Button button = (Button) findViewById(R.id.btnTryAgain);
        button.setOnClickListener(new View.OnClickListener()
			{
				public void onClick(View v)
				{
					// Perform action on click
					downloadXml();
				}
			});

		// Handler voor worker fragment
		FragmentManager fm = getSupportFragmentManager();
		mTaskFragment = (TaskFragment) fm.findFragmentByTag(TAG_TASK_FRAGMENT);

		// If the Fragment is non-null, then it is being retained
		// over a configuration change.
		if (mTaskFragment == null)
		{
			mTaskFragment = new TaskFragment();
			fm.beginTransaction().add(mTaskFragment, TAG_TASK_FRAGMENT).commit();
		}

		// Vind recyclerview en koppel layoutmanager
		mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
		mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

		// Als geen verbinding, toon knop
		// probeer opnieuw
		if (!isNetworkConnected())
		{
			tryAgain( getResources().getString(R.string.txt_try_again_nointernet) );
		}
    }

	// Toon foutboodschap
	// toon knop probeer opnieuw
	// eventueel cancel download
	// zet 	hasDownloaded flag op false
	private void tryAgain(String mMsg)
	{	
		TextView txtview = (TextView) findViewById(R.id.txtTryAgain);
		txtview.setText(mMsg);

		View viewTryAgain = findViewById(R.id.notConnectedLinLayout);
		viewTryAgain.setVisibility(View.VISIBLE);
		
		// verberg recyclerview
		View viewRecycler = findViewById(R.id.recycler_view);
		viewRecycler.setVisibility(View.GONE);

		Log.i("HermLog", "tryAgain()" );

		if (mTaskFragment.isRunning())
		{
			mTaskFragment.cancel();
		}
		
		mTaskFragment.setHasDownloaded(false);
	}

	// Start download json
	private void downloadXml()
	{
		// Als verbinding, download json
		if (isNetworkConnected())
		{
			// Als gestart door knop probeer opnieuw,
			// verberg knop
			View mNotConnectedLayout = findViewById(R.id.notConnectedLinLayout);
			mNotConnectedLayout.setVisibility(View.GONE);

			// en toon recyclerview
			View viewRecycler = findViewById(R.id.recycler_view);
			viewRecycler.setVisibility(View.VISIBLE);
			
			// Start asynchrone taak
			if ( !mTaskFragment.isRunning() )
			{
				mTaskFragment.start();
			}
		}
		else
		{
			tryAgain( getResources().getString(R.string.txt_try_again_nointernet) );
		}
	}

	// Zet json string per item in List<E>
	private void parseResult(String result)
	{
		Log.i("HermLog", "parseResult()" );
		
        try
		{
            JSONObject response = new JSONObject(result);
            JSONArray posts = response.optJSONArray("data");
            feedsList = new ArrayList<>();

            for (int i = 0; i < posts.length(); i++)
			{
                JSONObject post = posts.optJSONObject(i);
                FeedItem item = new FeedItem();
								
				// Velden in de lijst met feeditems vullen
                item.setTitle(post.optString("title"));
				// Datum opmaken
				String mDateString = post.optString("pubDate");
				try
				{
					Date mDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(mDateString);
					String mFormattedDate = DateFormat.getDateInstance(DateFormat.LONG).format(mDate);
					item.setPubdate(mFormattedDate);
				}
				catch (Exception e)
				{
					//
				}
				// einde datum opmaken
				item.setCreator(post.optString("creator"));
				item.setContent(post.optString("content"));
                item.setMediacontent(post.optString("mediacontent"));
				item.setMediawidth(post.optInt("mediawidth"));
				item.setMediaheight(post.optInt("mediaheight"));
                item.setMediamedium(post.optString("mediamedium"));
                item.setMediatype(post.optString("mediatype"));
				
                feedsList.add(item);
            }
        }
		catch (JSONException e)
		{
			Log.i("HermLog", "JSON Exception in parseResult" );
            e.printStackTrace();
        }
    }


	// Maak options menu in toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
	{
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
	{
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id)
		{
            case R.id.action_settings:
				return true;
        }

        return super.onOptionsItemSelected(item);
    }

	// Netwerkverbinding ja/nee
	private boolean isNetworkConnected()
	{
		ConnectivityManager connMgr = (ConnectivityManager)
			getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		return networkInfo != null && networkInfo.isConnected();
	}

	private void showSnackbar(String snackMsg)
	{
		// if ( findViewById(R.id.coordinator) )
		Snackbar mSnackbar = Snackbar.make(findViewById(R.id.coordinator), snackMsg, Snackbar.LENGTH_LONG);
		mSnackbar.show();
	}
	
	/*
	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		
		Log.i("HermLog", "onSaveInstanceState()" );
		
		outState.putParcelable(TAG_SCROLLPOSITION, mLinearLayoutManager.onSaveInstanceState() );
	}
	

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState)
	{
		super.onRestoreInstanceState(savedInstanceState);
		
		Log.i("HermLog", "onRestoreInstanceState()" );

    	mScrollposition = savedInstanceState.getParcelable(TAG_SCROLLPOSITION);
	}
	*/
	
	/*********************************/
	/***** TASK CALLBACK METHODS *****/
	/*********************************/

	@Override
	public void onPreExecute()
	{
		Log.i("HermLog", "onPreExecute()" );
		// Progressbar tonen
		View mProgressbar = findViewById(R.id.toolbar_progress_bar);
		mProgressbar.setVisibility(View.VISIBLE);
	}

	@Override
	public void onProgressUpdate(int percent)
	{
		// ...
	}

	@Override
	public void onCancelled()
	{
		// ...
		Log.i("HermLog", "onCancelled()" );
	}

	@Override
	public void onPostExecute(String mResult)
	{
		Log.i("HermLog", "onPostExecute()" );
		
		// Progressbar verbergen
		View mProgressbar = findViewById(R.id.toolbar_progress_bar);
		mProgressbar.setVisibility(View.GONE);

		// Als niets gedownload, toon boodschap
		// en knop probeer opnieuw
		if ( mResult == "Fout!" )
		{
			Log.i("HermLog", "Niets gedownload" );
			tryAgain( getResources().getString(R.string.txt_try_again_nodownload) );
			return;
		}		
		
		// List met xml maken, als gegevens aanwezig:
		// zet gedownload op ja
		parseResult(mResult);
		int responseSize = feedsList.size();
		
		if (responseSize > 0)
		{
			Log.i("HermLog", "Lengte List: " + String.valueOf(responseSize) );
			mTaskFragment.setHasDownloaded(true);

			// Verbind adapter met recyclerview
			adapter = new MyRecyclerViewAdapter(MainActivity.this, feedsList);
			mRecyclerView.setAdapter(adapter);

			// Actie bij klik op item
			adapter.setOnItemClickListener(new OnItemClickListener()
				{
					@Override
					public void onItemClick(FeedItem item)
					{
						Intent mIntent = new Intent(mContext, DetailActivity.class);
						
						mIntent.putExtra("mediacontent", item.getMediacontent() );
						mIntent.putExtra("title", item.getTitle() );
						mIntent.putExtra("pubdate", item.getPubdate() );
						mIntent.putExtra("creator", item.getCreator() );
						mIntent.putExtra("content", item.getContent() );

						mContext.startActivity(mIntent);
					}
				});
			
			// mTaskFragment.setHasRecyclerviewReady(true);
		}
	}

	/************************/
	/***** LOGS & STUFF *****/
	/************************/

	@Override
	protected void onStart()
	{
		Log.i("HermLog", "onStart()" );
		
		super.onStart();

		if (mTaskFragment.isRunning())
		{
			// Progressbar tonen als downloadproces nog loopt
			// na configuratie verandering
			View mProgressbar = findViewById(R.id.toolbar_progress_bar);
			mProgressbar.setVisibility(View.VISIBLE);
			return;
		}

		/*
		if ( !mTaskFragment.getHasRecyclerviewReady() )
		{
			downloadXml();
		}
		*/
		
		downloadXml();
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		
		/*
		Log.i("HermLog", "onResume()" );
		
		if (mScrollposition != null) {
			Log.i("HermLog", "mScrollposition" );
			mLinearLayoutManager.onRestoreInstanceState(mScrollposition);
		}
		*/
	}

	@Override
	protected void onPause()
	{
		super.onPause();
	}

	@Override
	protected void onStop()
	{
		super.onStop();
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
	}
}
