package nl.waywayway.broodkruimels;

import android.os.*;
import android.support.v7.app.*;
import android.support.v7.widget.*;

public class SettingsActivity extends AppCompatActivity
{
	@Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_preferences);
		
		// Maak toolbar
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_settings);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}
}
