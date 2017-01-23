package nl.waywayway.broodkruimels;

import android.os.*;
import android.support.v7.app.*;
import android.support.v7.widget.*;

public class DetailActivity extends AppCompatActivity
{
	@Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);

		// Maak toolbar
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_detail);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
