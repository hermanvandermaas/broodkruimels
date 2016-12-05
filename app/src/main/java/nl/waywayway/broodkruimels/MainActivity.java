package nl.waywayway.broodkruimels;

import android.content.*;
import android.net.*;
import android.os.*;
import android.support.design.widget.*;
import android.support.v4.app.*;
import android.support.v7.app.*;
import android.support.v7.widget.*;
import android.util.*;
import android.view.*;
import android.widget.*;

import android.support.v7.widget.Toolbar;

public class MainActivity extends AppCompatActivity implements TaskFragment.TaskCallbacks
{
	private static final String TAG = MainActivity.class.getSimpleName();
	private static final boolean DEBUG = true; // Set this to false to disable logs.
	private static final String KEY_CURRENT_PROGRESS = "current_progress";
	private static final String KEY_PERCENT_PROGRESS = "percent_progress";
	private static final String TAG_TASK_FRAGMENT = "task_fragment";
	private TaskFragment mTaskFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

		// Maak toolbar
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		ActionBar actionBar = getSupportActionBar();

		FragmentManager fm = getSupportFragmentManager();
		mTaskFragment = (TaskFragment) fm.findFragmentByTag(TAG_TASK_FRAGMENT);

		// If the Fragment is non-null, then it is being retained
		// over a configuration change.
		if (mTaskFragment == null)
		{
			mTaskFragment = new TaskFragment();
			fm.beginTransaction().add(mTaskFragment, TAG_TASK_FRAGMENT).commit();
		}
		
		// Controle netwerkverbinding en
		// download xml in methode onStart() onderaan
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

	private void showSnackbar()
	{
		Snackbar mSnackbar = (Snackbar) Snackbar.make(findViewById(R.id.coordinator), "Boem!", Snackbar.LENGTH_LONG);
		mSnackbar.show();
	}

	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		if (DEBUG) Log.i(TAG, "onSaveInstanceState(Bundle)");
		super.onSaveInstanceState(outState);
	}

	/*********************************/
	/***** TASK CALLBACK METHODS *****/
	/*********************************/

	@Override
	public void onPreExecute()
	{
		if (DEBUG) Log.i(TAG, "onPreExecute()");
		Toast.makeText(this, "onPreExecute", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onProgressUpdate(int percent)
	{
		if (DEBUG) Log.i(TAG, "onProgressUpdate(" + percent + "%)");
	}

	@Override
	public void onCancelled()
	{
		if (DEBUG) Log.i(TAG, "onCancelled()");
		Toast.makeText(this, "onCancelled", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onPostExecute()
	{
		if (DEBUG) Log.i(TAG, "onPostExecute()");
		Toast.makeText(this, "onPostExecute", Toast.LENGTH_SHORT).show();
	}

	/************************/
	/***** LOGS & STUFF *****/
	/************************/

	@Override
	protected void onStart()
	{
		if (DEBUG) Log.i(TAG, "onStart()");
		super.onStart();
		
		// Als verbinding, download xml
		// als geen verbinding, toon boodschap met knop probeer opnieuw
		if ( isNetworkConnected() )
		{
			// Start asynchrone taak
			if ( !mTaskFragment.isRunning() )
				mTaskFragment.start();
		} 
		else
		{
			// TODO: probeer opnieuw
		}
	}

	@Override
	protected void onResume()
	{
		if (DEBUG) Log.i(TAG, "onResume()");
		super.onResume();
	}

	@Override
	protected void onPause()
	{
		if (DEBUG) Log.i(TAG, "onPause()");
		super.onPause();
	}

	@Override
	protected void onStop()
	{
		if (DEBUG) Log.i(TAG, "onStop()");
		super.onStop();
	}

	@Override
	protected void onDestroy()
	{
		if (DEBUG) Log.i(TAG, "onDestroy()");
		super.onDestroy();
	}
}
