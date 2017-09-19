package nl.waywayway.broodkruimels;

import android.app.*;
import android.content.*;
import android.net.*;
import android.os.*;
import android.support.design.widget.*;
import android.support.v4.app.*;
import android.support.v7.app.*;
import android.support.v7.preference.*;
import android.support.v7.widget.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import com.google.android.gms.common.*;
import com.paginate.*;
import java.text.*;
import java.util.*;
import org.json.*;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

public class MainActivity extends AppCompatActivity implements TaskFragment.TaskCallbacks, CategoryDialogFragment.DownloadCategories
{
	private static final String TAG_TASK_FRAGMENT = "task_fragment";
	private static final String KEY_PREF_CATEGORIES = "pref_categories";
	private static final String FILENAME_PREF_CATEGORIES = "categories";
	private ActionBar actionBar;
	private TaskFragment mTaskFragment;
	private List<FeedItem> feedsList;
	private List<CategoryItem> categoryList;
	private boolean dialogWasShowed = false;
	private RecyclerView mRecyclerView;
	private String mScreenWidth;
	private LinearLayoutManager mLinearLayoutManager;
	private StaggeredGridLayoutManager mStaggeredGridLayoutManager;
	private MyRecyclerViewAdapter adapter;
	private Context mContext;
	private float mLogicalDensity;
	private int mColumnWidth;
	private boolean pageLoadingInProgress;
	private boolean hasLoadedAllItems;
	private int recyclerViewListSize = 0;
	int appWidthDp;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Log.i("HermLog", "onCreate()");

		// zet referentie naar context van deze activity in een variabele
		mContext = this;

		// maak lege feedsList en categoryList aan
		feedsList = new ArrayList<>();
		categoryList = new ArrayList<>();

		// Maak toolbar
		makeToolbar();

		// Actie bij klik op knop probeer opnieuw
		setClickActionTryAgain();

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

		// Als extra data voor endless scrolling nog aan het downloaden is na bv. schermrotatie (configuration change),
		// stop downloaden (want eerst moet eerste 'page' downloaden), daarbij wordt getExtraPage op false gezet om
		// om aan te geven wat de status van de taskfragment is: eerste page downloaden of extra data voor endless scrolling
		if (mTaskFragment.isRunning() && mTaskFragment.getGetExtraPage())
		{
			mTaskFragment.cancel();
		}

		// Als geen verbinding, toon knop
		// probeer opnieuw
		if (!isNetworkConnected())
		{
			tryAgain(getResources().getString(R.string.txt_try_again_nointernet));
		}
	}

	private String getCategories()
	{
		SharedPreferences sharedPref = mContext.getSharedPreferences(this.FILENAME_PREF_CATEGORIES, mContext.MODE_PRIVATE);
		String prefDefault = "";
		String savedCategoriesString = sharedPref.getString(this.KEY_PREF_CATEGORIES, prefDefault);
		Log.i("HermLog", "MainActivity: savedCategoriesString: " + savedCategoriesString);
		String commaSeparatedList = savedCategoriesString.replaceAll("\\[|\\]", "").replaceAll("\\s", "");
		// Log.i("HermLog", "CommaSeparatedList: " + commaSeparatedList);

		return commaSeparatedList;
	}

	// Check beschikbaarheid Play Services
	protected void isPlayServicesAvailable()
	{
		if (dialogWasShowed) return;

		GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
		int resultCode = apiAvailability.isGooglePlayServicesAvailable(mContext);

		if (resultCode != ConnectionResult.SUCCESS)
		{
			Log.i("HermLog", "Play Services fout");
			if (apiAvailability.isUserResolvableError(resultCode))
			{
				apiAvailability.getErrorDialog((Activity) mContext, resultCode, 9000).show();
				dialogWasShowed = true;
			}
		}
	}

	// Maak toolbar
	private void makeToolbar()
	{
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		ActionBar actionBar = getSupportActionBar();
	}

	// Klik knop probeer opnieuw:
	// check verbinding, indien ok dan json laden
	private void setClickActionTryAgain()
	{
		Button button = (Button) findViewById(R.id.btnTryAgain);
		button.setOnClickListener(new View.OnClickListener()
			{
				public void onClick(View v)
				{
					// Perform action on click
					downloadXml(false);
				}
			});
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

		Log.i("HermLog", "tryAgain()");

		if (mTaskFragment.isRunning())
		{
			mTaskFragment.cancel();
		}
	}

	// Check of deze activity is gestart vanuit een push melding,
	// zo ja, start de juiste andere activity, op basis van de inhoud van de melding
	private void ifStartedFromPushNotificationStartOtherActivity()
	{
		if (getIntent().getExtras() != null)
		{
			boolean used = getIntent().getExtras().getBoolean("used");

			// Haal data uit Extras van de intent
			for (String key : getIntent().getExtras().keySet())
			{
				Object value = getIntent().getExtras().get(key);
				Log.i("HermLog", "Key: " + key + ", Value: " + value);

				// Toast.makeText(mContext, "Key: " + key + "Value: " + value, Toast.LENGTH_SHORT).show();

				// In de Extras van de Intent moet een sleutel "url" staan
				// zo ja, dan wordt de Activity 'DetailActivity' opgestart
				if (key.equalsIgnoreCase("url") && !used)
				{
					// Tag de Intent dat deze is gebruikt. 
					// Als in de Intent extras staan vanuit een push
					// notification, worden deze opgemerkt in MainActivity
					// en stuurt MainActivity telkens terug naar DetailActivity.
					// Deze tag voorkomt dit.

					getIntent().putExtra("used", true);

					// Start activity
					Intent mIntent = new Intent(mContext, DetailActivity.class);
					mContext.startActivity(mIntent);
				}
			}
		}
	}

	// Start download json (was eerst xml, vandaar de method naam)
	public void downloadXml(Boolean downloadMoreItems)
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
			if (!mTaskFragment.isRunning())
			{
				// Geef bestaande lijstgrootte door, voor aanvullend data downloaden bij endless scrolling
				mTaskFragment.setFeedsListSize(feedsList.size());

				// Geef te downloaden categorieen door
				mTaskFragment.setCategoriesParameter(getCategories());

				// Bij eerste download van items start(false)
				// bij latere download van extra items start(true)
				mTaskFragment.start(downloadMoreItems);
			}
			else
			{
				Log.i("HermLog", "mTaskFragment.isRunning(): " + mTaskFragment.isRunning());
			}
		}
		// Als geen verbinding, toon knop voor opnieuw proberen
		else
		{
			tryAgain(getResources().getString(R.string.txt_try_again_nointernet));
		}
	}

	@Override
	public void downloadFromCategories()
	{
		Log.i("HermLog", "downloadFromCategories");
					
		// Cancel eventueel lopende download
		if (mTaskFragment.isRunning())
		{
			mTaskFragment.cancel();
		}
		
		// Reset endless scrolling flags
		pageLoadingInProgress = false;
		hasLoadedAllItems = false;

		// Wis eerder gedownloade data
		feedsList.clear();
		categoryList.clear();
		
		// Verberg categorie knop
		// deze method roept onCreateOptionsMenu() aan
		if (categoryList.size() > 0)
		{
			invalidateOptionsMenu();
		}
		
		downloadXml(false);
	}

	// Datum opmaken
	private String formatDate(String mDateString, String dateFormat)
	{
		try
		{
			Date mDate = new SimpleDateFormat(dateFormat).parse(mDateString);
			String mFormattedDate = DateFormat.getDateInstance(DateFormat.LONG).format(mDate);
			return mFormattedDate;
		}
		catch (Exception e)
		{
			Log.i("HermLog", "Date format exception in parseResult");
			e.printStackTrace();
		}

		return "";
	}


	// json string verwerken na download
	// Zet json string per item in List<E>
	private void parseResult(String result, Boolean downloadMoreItems)
	{
		Log.i("HermLog", "parseResult()");

		try
		{
			// Content items
			JSONObject response = new JSONObject(result);
			JSONArray posts = response.optJSONArray("data");

			for (int i = 0; i < posts.length(); i++)
			{
				JSONObject post = posts.optJSONObject(i);
				FeedItem item = new FeedItem();

				// Velden in de lijst met feeditems vullen
				item.setTitle(post.optString("title"));
				item.setLink(post.optString("link"));
				item.setPubdate(formatDate(post.optString("pubDate"), "yyyy-MM-dd HH:mm:ss"));
				item.setCreator(post.optString("creator"));
				item.setContent(post.optString("content"));
				item.setMediacontent(post.optString("mediacontent"));
				item.setMediawidth(post.optInt("mediawidth"));
				item.setMediaheight(post.optInt("mediaheight"));
				item.setMediamedium(post.optString("mediamedium"));
				item.setMediatype(post.optString("mediatype"));
				item.setImgwidth(post.optInt("imgwidth"));
				item.setImgheight(post.optInt("imgheight"));

				feedsList.add(item);
			}

			// Categorieen alleen parsen als de lijst met categorieen leeg is
			if (categoryList.size() > 0) return;
			
			// Categorieen
			JSONArray categories = response.optJSONArray("categories");

			for (int i = 0; i < categories.length(); i++)
			{
				JSONObject category = categories.optJSONObject(i);
				CategoryItem categoryItem = new CategoryItem();

				// Velden in de lijst met categoryItems vullen
				categoryItem.setNumber(category.optInt("number"));
				categoryItem.setName(category.optString("name"));
				categoryItem.setParent(category.optInt("parent"));

				categoryList.add(categoryItem);
			}
		}
		catch (JSONException e)
		{
			Log.i("HermLog", "JSON Exception in parseResult");
			e.printStackTrace();
		}
	}


	// Maak options menu in toolbar
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		MenuItem categoryItem = menu.findItem(R.id.action_select_category);

		Log.i("HermLog", "categoryList.size(): " + categoryList.size());

		// Toon categorie knop, als content beschikbaar is
		if (categoryList.size() > 0)
		{
			categoryItem.setVisible(true);
		}
		else
		{
			categoryItem.setVisible(false);
		}


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
				// Ga naar instellingen / preferences / settings scherm
				Intent mIntent = new Intent(mContext, SettingsActivity.class);
				mIntent.putExtra("categoryListExtra", (ArrayList<CategoryItem>) categoryList);
				mContext.startActivity(mIntent);
				return true;

			case R.id.action_select_category:
				showCategoryDialog();
				return true;

			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void showCategoryDialog()
	{
		CategoryDialogFragment categoryDialog = new CategoryDialogFragment();
		categoryDialog.setCategoryList((ArrayList<CategoryItem>) categoryList);
		categoryDialog.setPrefFilename(this.FILENAME_PREF_CATEGORIES);
		categoryDialog.setPrefKey(this.KEY_PREF_CATEGORIES);
		categoryDialog.show(getSupportFragmentManager(), "category");
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

	/*********************************/
	/***** TASK CALLBACK METHODS *****/
	/*********************************/

	@Override
	public void onPreExecute()
	{
		Log.i("HermLog", "onPreExecute()");

		// Appbar tonen, zodat progressbar (draaiende cirkel) zichtbaar wordt
		// tijdens laden data
		AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbarlayout);
		appBarLayout.setExpanded(true, true);

		// Progressbar tonen
		showProgressBar();
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
		Log.i("HermLog", "onCancelled()");
	}

	@Override
	public void onPostExecute(String mResult, Boolean downloadMoreItems)
	{
		Log.i("HermLog", "onPostExecute() downloadMoreItems: " + downloadMoreItems);

		// Verberg progress bar
		hideProgressBar();

		// Als extra data zijn gedownload voor endless scrolling,
		// maar eerste 'pagina' met data staat niet (meer) in de lijst
		// bv. vanwege tussentijdse schermrotatie (configuration change),
		// dan data niet toevoegen aan lijst
		if (!(feedsList.size() > 0) && downloadMoreItems)
		{
			Log.i("HermLog", "Extra data, maar geen bestaande data");
			downloadMoreItems = false;
			downloadXml(false);
			return;
		}

		// Als niets gedownload, toon boodschap
		// en knop probeer opnieuw
		if (mResult == "Fout!")
		{
			Log.i("HermLog", "Niets gedownload");
			tryAgain(getResources().getString(R.string.txt_try_again_nodownload));
			return;
		}		

		// Als download blijkbaar goed is gegaan
		// resultaat parsen in een arraylist
		parseResult(mResult, downloadMoreItems);

		// Data zijn beschikbaar, toon categorie knop
		// deze method roept onCreateOptionsMenu() aan
		if (categoryList.size() > 0)
		{
			invalidateOptionsMenu();
		}

		// Als er geen extra data meer zijn gedownload,
		// geef aan dat laatste 'page' met data is gedownload
		// voor endless scrolling
		// en return
		if ((recyclerViewListSize == feedsList.size()) && downloadMoreItems)
		{
			pageLoadingInProgress = false;
			hasLoadedAllItems = true;
			return;
		}

		// Als data aanwezig zijn in de lijst,
		// en deze data komen uit de eerste download (niet extra 'page' voor endless scrolling),
		// maak recyclerview
		if (feedsList.size() > 0 && !downloadMoreItems)
		{
			Log.i("HermLog", "Lengte List: " + String.valueOf(feedsList.size()));

			makeRecyclerView();

			// recyclerViewListSize is voor endless scrolling, bijhouden van
			// lengte datalijst die bekend is bij recyclerview adapter
			recyclerViewListSize = feedsList.size();
		}

		// Als data aanwezig zijn in de lijst,
		// en deze data komen uit extra 'page' voor endless scrolling),
		// update recyclerview met extra data
		if (feedsList.size() > 0 && downloadMoreItems)
		{
			Log.i("HermLog", "Update recyclerview met extra data");

			// Geef toevoeging aan listarray van gedownloade items door aan recyclerview adapter
			// Zodat ze zichtbaar worden in recyclerview, met animatie
			adapter.notifyItemRangeInserted(recyclerViewListSize, feedsList.size() - recyclerViewListSize);

			// recyclerViewListSize is voor endless scrolling, bijhouden van
			// lengte datalijst die bekend is bij recyclerview adapter
			recyclerViewListSize = feedsList.size();

			// Volgende 'page' downloaden mag vanaf nu weer
			pageLoadingInProgress = false;
		}
	}

	// Progressbar tonen
	private void showProgressBar()
	{
		View mProgressbar = findViewById(R.id.toolbar_progress_bar);
		mProgressbar.setVisibility(View.VISIBLE);
		return;
	}

	// Progressbar verbergen
	private void hideProgressBar()
	{
		View mProgressbar = findViewById(R.id.toolbar_progress_bar);
		mProgressbar.setVisibility(View.GONE);
	}

	// Bepaal breedte van de app: 'narrow' of 'width'
	// grenswaarde staat in xml bestand resources / values / integers
	private void findAppWidth()
	{
		// Vind breedte van de app in dp
		// dp = pixels / logical density
		// de gemeten breedte is de breedte van de hoogste view in het xml layout bestand
		View mView = findViewById(R.id.coordinator);
		int mViewWidth = mView.getWidth();
		DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
		mLogicalDensity = displayMetrics.density;
		appWidthDp = Math.round(mViewWidth / mLogicalDensity);

		// Bepaal of de weergave van de app smal of breed is
		if (appWidthDp <= getResources().getInteger(R.integer.listview_max_width))
			this.mScreenWidth = "narrow";
		else
			this.mScreenWidth = "wide";

		Log.i("HermLog", "AppWidthDp: " + appWidthDp);
		Log.i("HermLog", "Density: " + displayMetrics.density);
	}

	private int getNumberOfColumns()
	{
		// Bereken aantal kolommen
		// Kolombreedte staat in xml bestand resources / values / integers
		mColumnWidth = getResources().getInteger(R.integer.staggeredgridview_column_width);
		int mNumberOfColumns = Math.round((float) appWidthDp / mColumnWidth);

		// Log.i("HermLog", "mNumberOfColumns: " + mNumberOfColumns);

		return mNumberOfColumns;
	}

	// 
	private void setLayoutManager()
	{
		if (this.mScreenWidth == "narrow")
		{
			// Koppel layoutmanager voor smal scherm
			mLinearLayoutManager = new LinearLayoutManager(this);
			mRecyclerView.setLayoutManager(mLinearLayoutManager);
		}
		else
		{
			// Koppel layoutmanager voor breed scherm
			mStaggeredGridLayoutManager = new StaggeredGridLayoutManager(getNumberOfColumns(), StaggeredGridLayoutManager.VERTICAL);
			mRecyclerView.setLayoutManager(mStaggeredGridLayoutManager);
		}
	}

	// Koppel adapter
	private void setAdapter()
	{
		// maak adapter instance
		adapter = new MyRecyclerViewAdapter(MainActivity.this, feedsList);

		// instelling appbreedte en logical density in adapter,
		// voor berekenen van aantal kolommen en aanpassen afbeelding in staggered grid layout
		// oncreateviewholder en onbindviewholder worden na deze setters aangeroepen
		adapter.setColumnWidth(mColumnWidth);
		adapter.setScreenWidth(mScreenWidth);
		adapter.setLogicalDensity(mLogicalDensity);

		// Verbind adapter met recyclerview
		mRecyclerView.setAdapter(adapter);
	}

	// Endless scrolling
	// met library 'Paginate'
	private void endlessScrolling()
	{
		// endless scrolling
		Paginate.Callbacks callbacks = new Paginate.Callbacks() {
			@Override
			public void onLoadMore()
			{
				Log.i("HermLog", "in onLoadMore() isLoading(): " + isLoading());
				Log.i("HermLog", "in onLoadMore() hasLoadedAllItems(): " + hasLoadedAllItems());

				if (hasLoadedAllItems) return;
				if (pageLoadingInProgress) return;

				// Laad volgende 'page' met data
				pageLoadingInProgress = true;
				downloadXml(true);
			}

			@Override
			public boolean isLoading()
			{
				// Indicate whether new page loading is in progress or not
				Log.i("HermLog", "pageLoadingInProgress: " + pageLoadingInProgress);
				return pageLoadingInProgress;
			}

			@Override
			public boolean hasLoadedAllItems()
			{
				// Indicate whether all data (pages) are loaded or not
				Log.i("HermLog", "hasLoadedAllItems: " + hasLoadedAllItems);
				return hasLoadedAllItems;
			}
		};

		// Drempelwaarde hoe ver van einde van de lijst voor extra data downloaden
		int endlessScrollingThreshold = getResources().getInteger(R.integer.endlessScrollingThreshold);

		// endless scrolling
		Paginate.with(mRecyclerView, callbacks)
			.setLoadingTriggerThreshold(endlessScrollingThreshold)
			.addLoadingListItem(false)
			.build();
	}

	// Actie bij klik op item
	private void setClickAction()
	{
		// Actie bij klik op item
		adapter.setOnItemClickListener(new OnItemClickListener()
			{
				@Override
				public void onItemClick(FeedItem item)
				{
					Intent mIntent = new Intent(mContext, DetailActivity.class);

					mIntent.putExtra("mediacontent", item.getMediacontent());
					mIntent.putExtra("imgwidth", item.getImgwidth());
					mIntent.putExtra("imgheight", item.getImgheight());
					mIntent.putExtra("title", item.getTitle());
					mIntent.putExtra("link", item.getLink());
					mIntent.putExtra("pubdate", item.getPubdate());
					mIntent.putExtra("creator", item.getCreator());
					mIntent.putExtra("content", item.getContent());

					mContext.startActivity(mIntent);
				}
			});
	}

	// Vul recyclerview:
	// Koppel layoutmanager, verbind adapter, 
	// instellen endless scrolling, actie bij aanklikken item
	private void makeRecyclerView()
	{
		// Bepaal breedte van de app: 'narrow' of 'wide'
		findAppWidth();

		// Vind recyclerview
		mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

		// Koppel juiste layoutmanager
		setLayoutManager();

		// Koppel adapter
		setAdapter();

		// Endless scrolling
		endlessScrolling();

		// Actie bij klik op item
		setClickAction();
	}

	/************************/
	/***** LOGS & STUFF *****/
	/************************/

	@Override
	protected void onNewIntent(Intent intent)
	{
		super.onNewIntent(intent);
		Log.i("HermLog", "onNewIntent()");

		setIntent(intent);
	}

	@Override
	protected void onStart()
	{
		super.onStart();

		Log.i("HermLog", "onStart()");

		// Progressbar tonen als downloadproces nog loopt
		// na configuratie verandering
		if (mTaskFragment.isRunning())
		{
			showProgressBar();
		}

		Log.i("HermLog", "feedsList.size(): " + feedsList.size());

		// Data downloaden, behalve als er al data in de feedslist staan
		if (!(feedsList.size() > 0))
		{
			downloadXml(false);
		}
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		Log.i("HermLog", "onResume()");

		// Check beschikbaarheid Google Play services
		// is nodig voor Push notifications
		isPlayServicesAvailable();

		// Check of deze activity is gestart vanuit een push melding,
		// zo ja, start de juiste andere activity, op basis van de inhoud van de melding
		ifStartedFromPushNotificationStartOtherActivity();
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		Log.i("HermLog", "onPause()");
	}

	@Override
	protected void onStop()
	{
		super.onStop();
		Log.i("HermLog", "onStop()");
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		Log.i("HermLog", "onDestroy()");
	}
}
