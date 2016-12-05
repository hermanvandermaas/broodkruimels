package nl.waywayway.broodkruimels;

import android.content.*;
import android.net.*;
import android.os.*;
import android.support.design.widget.*;
import android.support.v7.app.*;
import android.support.v7.widget.*;
import android.view.*;

public class MainActivity extends AppCompatActivity 
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

		// Maak toolbar
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		ActionBar actionBar = getSupportActionBar();

		// Als verbinding, download xml
		// als geen verbinding, toon boodschap met knop probeer opnieuw
		if (isNetworkConnected())
		{
			// downloadXml();
		} 
		else
		{
			// tryAgain();
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

	private void showSnackbar()
	{
		Snackbar mSnackbar = (Snackbar) Snackbar.make(findViewById(R.id.coordinator), "Boem!", Snackbar.LENGTH_LONG);
		mSnackbar.show();
	}

}
