package nl.waywayway.broodkruimels;

import android.os.*;
import android.support.v4.app.*;
import android.support.v7.app.*;
import android.support.v7.widget.*;
import android.util.*;
import java.util.*;

public class SettingsActivity extends AppCompatActivity implements CategoryDialogFragment.DownloadCategories
{
	public static final String KEY_PREF_NOTIFY = "pref_notify";
	public static final String KEY_PREF_NOTIFY_TIME = "pref_notify_time";
	public static final String KEY_PREF_NOTIFY_CATEGORIES = "pref_notify_categories";
	private List<CategoryItem> categoryList;
	SettingsFragment mSettingsFragment;
	
	@Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_preferences);
		
		// Handler voor settings fragment
		FragmentManager fm = getSupportFragmentManager();
		mSettingsFragment = (SettingsFragment) fm.findFragmentById(R.id.settings_fragment);
		Log.i("HermLog", "mSettingsFragment: " + mSettingsFragment);
		
		// Haal ArrayList met categorieen uit intent, geef door aan fragment
		categoryList = (ArrayList<CategoryItem>) getIntent().getSerializableExtra("categoryListExtra");
		mSettingsFragment.setCategoryList(categoryList);
		
		// Maak toolbar
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_settings);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	protected void onStart()
	{
		super.onStart();
	}
	
	@Override
	public void downloadFromCategories()
	{
		// TODO: Implement this method
	}
}
