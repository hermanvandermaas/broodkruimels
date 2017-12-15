package nl.waywayway.broodkruimels;

import android.content.*;
import android.net.*;
import android.os.*;
import android.support.v4.app.*;
import android.support.v7.app.*;
import android.support.v7.widget.*;
import android.text.*;
import android.text.method.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import com.squareup.picasso.*;
import java.util.*;
import nl.waywayway.broodkruimels.*;

import android.support.v7.widget.Toolbar;

public class DetailActivity extends AppCompatActivity implements TaskFragment.TaskCallbacks
{
	private static final String TAG_TASK_FRAGMENT = "task_fragment";
	private Boolean weHaveData = false;
	private Intent intent;
	private String imageUrl;
	private TaskFragment taskFragment;
	private ArrayList<FeedItem> feedsList;
	private int imgWidth;
	private int imgHeight;
	private String title;
	private String link;
	private String pubDate;
	private String creator;
	private String content;
	private Context context;
	private ImageView imageView;
	private ProgressBar progressBar;
	private TextView textViewTitle;
	private TextView textViewPubdate;
	private TextView textViewCreator;
	private TextView textViewContent;
	private int urlWidth;

	@Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_detail);

		// zet referentie naar context van deze activity in een variabele
		context = this;

		// Maak toolbar
		makeToolBar();

		// Probeer data uit intent te halen
		// indien data aanwezig dan wordt deze activity gestart
		// vanuit de lijst en zijn data aanwezig;
		// Indien data niet aanwezig dan wordt deze activity blijkbaar
		// gestart vanuit een notification en moeten data nog
		// worden downgeload
		getDataFromIntent();
		if (!TextUtils.isEmpty(imageUrl))
			weHaveData = true;

		Log.i("HermLog", "mWeHaveData: " + weHaveData);

		if (weHaveData)
		{
			// Download afbeelding
			downloadImage(false);

			// Zet tekst in textviews
			setText();
		}

		// Actie bij klik op knop probeer opnieuw
		setClickActionTryAgain();

		// Open Activity voor fullscreen afbeelding bij klik 
		// op kleinere afbeelding
		setImageClickAction();

		// Handler voor worker fragment
		FragmentManager fm = getSupportFragmentManager();
		taskFragment = (TaskFragment) fm.findFragmentByTag(TAG_TASK_FRAGMENT);

		// If the Fragment is non-null, then it is being retained
		// over a configuration change.
		if (taskFragment == null)
		{
			taskFragment = new TaskFragment();
			fm.beginTransaction().add(taskFragment, TAG_TASK_FRAGMENT).commit();
		}
    }

	private void makeToolBar()
	{
		// Maak toolbar
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_detail);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	// Maak options menu in toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
	{
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
		MenuItem shareItem = menu.findItem(R.id.action_share);

		// Toon share knop, als content beschikbaar is
		if (!TextUtils.isEmpty(link))
		{
			// Toast.makeText(mContext, "mLink: " + mLink, Toast.LENGTH_SHORT).show();
			shareItem.setVisible(true);
		}
		else
		{
			shareItem.setVisible(false);
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
            case R.id.action_share:
				// Deel content via andere app
				// Toast.makeText(mContext, "mLink: " + mLink, Toast.LENGTH_SHORT).show();
				share();
				return true;

			default:
				return super.onOptionsItemSelected(item);
        }
    }

	private void setClickActionTryAgain()
	{
		// Klik knop probeer opnieuw:
		// check verbinding, indien ok dan json laden
		Button button = (Button) findViewById(R.id.btnTryAgain_detailactivity);
		button.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					downloadXml();
				}
			});
	}

	// Deel content via andere app
	private void share()
	{		
		Intent shareIntent = ShareCompat.IntentBuilder.from(DetailActivity.this)
			.setType("text/plain")
			.setText(link)
			.setSubject(title)
			.getIntent();

		if (shareIntent.resolveActivity(getPackageManager()) != null)
		{
			startActivity(shareIntent);
		}
	}

	// Haal data uit intent
	private void getDataFromIntent()
	{
		Log.i("HermLog", "getDataFromIntent()");

		intent = getIntent();
		imageUrl = intent.getStringExtra("mediacontent");
		// mImgWidth en mImgHeight zijn afmetingen van de oorspronkelijke niet verkleinde afbeelding
		imgWidth = intent.getIntExtra("imgwidth", 1);
		imgHeight = intent.getIntExtra("imgheight", 1);
		title = intent.getStringExtra("title");
		link = intent.getStringExtra("link");
		pubDate = intent.getStringExtra("pubdate");
		creator = intent.getStringExtra("creator");
		content = intent.getStringExtra("content");
	}

	// Start download json (was eerst xml, vandaar de method naam)
	private void downloadXml()
	{
		Log.i("HermLog", "DetailActivity: downloadXml()");

		// Als verbinding, download json
		if (isNetworkConnected())
		{
			// Als gestart door knop probeer opnieuw,
			// verberg knop
			View mNotConnectedLayout = findViewById(R.id.notConnectedLinLayout_detailactivity);
			mNotConnectedLayout.setVisibility(View.GONE);

			// Toon nestedscrollview
			View viewNestedScroll = findViewById(R.id.nestedscrollview_detail);
			viewNestedScroll.setVisibility(View.VISIBLE);

			// Start asynchrone taak
			if (!taskFragment.isRunning())
			{
				// Geef te downloaden categorieen door
				CategoryGetter categoryGetter = new CategoryGetter(context, SettingsFragment.FRAGMENT_FILENAME_PREF_NOTIFY_CATEGORIES , SettingsFragment.FRAGMENT_KEY_PREF_NOTIFY_CATEGORIES);
				taskFragment.setCategoriesParameter(categoryGetter.getCategories());

				// Bij eerste download van items start(false)
				// bij latere download van extra items (niet aan de
				// orde in deze class) start(true)
				taskFragment.start(false, feedsList, null);
			}
			else
			{
				Log.i("HermLog", "DetailActivity: mTaskFragment.isRunning(): " + taskFragment.isRunning());
			}
		}
		// Als geen verbinding, toon knop voor opnieuw proberen
		else
		{
			Log.i("HermLog", "DetailActivity: geen verbinding");
			tryAgain(getResources().getString(R.string.txt_try_again_nointernet));
		}
	}

	// Netwerkverbinding ja/nee
	private boolean isNetworkConnected()
	{
		ConnectivityManager connMgr = (ConnectivityManager)
			getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		return networkInfo != null && networkInfo.isConnected();
	}

	// Toon foutboodschap
	// toon knop probeer opnieuw
	// eventueel cancel download
	private void tryAgain(String mMsg)
	{	
		TextView txtview = (TextView) findViewById(R.id.txtTryAgain_detailactivity);
		txtview.setText(mMsg);

		View viewTryAgain = findViewById(R.id.notConnectedLinLayout_detailactivity);
		viewTryAgain.setVisibility(View.VISIBLE);

		// verberg nestedscrollview
		View viewNestedScroll = findViewById(R.id.nestedscrollview_detail);
		viewNestedScroll.setVisibility(View.GONE);

		Log.i("HermLog", "DetailActivity: tryAgain()");

		if (taskFragment.isRunning())
		{
			taskFragment.cancel();
		}
	}

	// Download image using picasso library
	// parameter secondTry geeft aan of eerste download mislukt is,
	// zo ja, dan oorspronkelijke (mogelijk erg grote)
	// afbeelding proberen te downloaden
	private void downloadImage(Boolean secondTry)
	{
		imageView = (ImageView) findViewById(R.id.image_detail);
		
		// Maak url afbeelding
		urlWidth = getResources().getInteger(R.integer.activity_detail_image_size);
		MakeImageUrl imgUrlMaker = new MakeImageUrl(imgWidth, imgHeight, urlWidth, imageUrl);
		
		// Poging 1
		if (!secondTry)
		{
			// progress bar indeterminate (draaiende cirkel)
			// zichtbaar maken tijdens downloaden afbeelding
			progressBar = (ProgressBar) findViewById(R.id.image_detail_progress_bar);
			progressBar.setVisibility(ProgressBar.VISIBLE);

			// Laad afbeelding
			Picasso
				.with(context)
				.load(imgUrlMaker.make(secondTry))
				.into(imageView, new Callback()
				{
					@Override
					public void onSuccess()
					{
						progressBar.setVisibility(ProgressBar.GONE);
					}

					@Override
					public void onError()
					{
						Log.i("HermLog", "1e poging: afbeelding downloadfout");
						downloadImage(true);
					}
				});				
		}
		else
		{
			// Poging 2
			// Laad grote afbeelding, onverkleind
			Picasso
				.with(context)
				.load(imgUrlMaker.make(secondTry))
				.resize(urlWidth, 0)
				.into(imageView, new Callback()
				{
					@Override
					public void onSuccess()
					{
						progressBar.setVisibility(ProgressBar.GONE);
					}

					@Override
					public void onError()
					{
						progressBar.setVisibility(ProgressBar.GONE);
						Log.i("HermLog", "2e poging: afbeelding downloadfout");
					}
				});
		}
	}

	// Actie bij klik op afbeelding
	private void setImageClickAction()
	{
		ImageView mImage = (ImageView) findViewById(R.id.image_detail);
        mImage.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					Intent mImageIntent = new Intent(context, ImageActivity.class);

					mImageIntent.putExtra("mediacontent", imageUrl);
					mImageIntent.putExtra("imgwidth", imgWidth);
					mImageIntent.putExtra("imgheight", imgHeight);

					context.startActivity(mImageIntent);
				}
			});
	}

	private void setText()
	{
		// Vind text views
		// title-pubdate-creator-content
		textViewTitle = (TextView) findViewById(R.id.title_detail);
		textViewPubdate = (TextView) findViewById(R.id.pubdate_detail);
		textViewCreator = (TextView) findViewById(R.id.creator_detail);
		textViewContent = (TextView) findViewById(R.id.content_detail);

		// Setting text views
		textViewTitle.setText(Html.fromHtml(title));
		textViewPubdate.setText(Html.fromHtml(pubDate));
		textViewCreator.setText(Html.fromHtml(creator));
		textViewContent.setText(Html.fromHtml(makeVideoLinks(content)));
		// Maak links klikbaar
		textViewContent.setMovementMethod(LinkMovementMethod.getInstance());
	}

	// Maak klikbare links van embedded video (youtube) iframes
	private String makeVideoLinks(String myHtml)
	{
		if (TextUtils.isEmpty(myHtml)) return myHtml;

		String pattern = "<iframe\\s+.*?\\s+src=(\".*?\").*?<\\/iframe>";
		String watchVideo = getResources().getString(R.string.txt_watch_video);
		String replacement = "<a href=$1>" + watchVideo + "</a>";

		return myHtml.replaceAll(pattern, replacement);
	}

	/*********************************/
	/***** TASK CALLBACK METHODS *****/
	/*********************************/

	@Override
	public void onPreExecute()
	{
		Log.i("HermLog", "DetailActivity: onPreExecute()");

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
		Log.i("HermLog", "DetailActivity: onCancelled()");
	}

	@Override
	public void onPostExecute(String mResult, Boolean downloadMoreItems, ArrayList<FeedItem> feedsListLatest, ArrayList<CategoryItem> categoryListLatest)
	{
		Log.i("HermLog", "DetailActivity: onPostExecute()");

		// Verberg progress bar
		hideProgressBar();

		// Als niets gedownload, toon boodschap
		// en knop probeer opnieuw
		if (mResult.equalsIgnoreCase("Fout in DownloadJsonString!"))
		{
			Log.i("HermLog", "DetailActivity: Niets gedownload");
			tryAgain(getResources().getString(R.string.txt_try_again_nodownload));
			return;
		}

		// Neem bijgewerkte feedsList en categoryList over in deze Activity
		// zet data uit ArrayList in fields
		feedsList = feedsListLatest;
		parseList(feedsListLatest);

		if (!TextUtils.isEmpty(imageUrl))
		{
			weHaveData = true;

			// Data zijn beschikbaar, toon share knop
			// deze method roept onCreateOptionsMenu() aan
			invalidateOptionsMenu();
		}

		// Als data aanwezig zijn in de lijst,
		// update de UI met de data
		if (!TextUtils.isEmpty(imageUrl))
		{
			Log.i("HermLog", "DetailActivity: data gedownload, update UI");

			// Download afbeelding
			downloadImage(false);

			// Zet tekst in textviews
			setText();
		}
	}

	// Zet data in variabelen
	private void parseList(ArrayList<FeedItem> downloadedFeedsList)
	{
		Log.i("HermLog", "DetailActivity: parseResult()");
		FeedItem feedItem = downloadedFeedsList.get(0);
		
		imageUrl = feedItem.getMediacontent();
		// mImgWidth en mImgHeight zijn afmetingen van de oorspronkelijke niet verkleinde afbeelding
		imgWidth = feedItem.getImgwidth();
		imgHeight = feedItem.getImgheight();
		title = feedItem.getTitle();
		link = feedItem.getLink();
		pubDate = DateStringFormatter.getDateStringFormatter().formatDate(feedItem.getPubDate(), "yyyy-MM-dd HH:mm:ss");
		creator = feedItem.getCreator();
		content = feedItem.getContent();
    }

	// Progressbar tonen
	private void showProgressBar()
	{
		View mProgressbar = findViewById(R.id.toolbar_progress_bar_detailactivity);
		mProgressbar.setVisibility(View.VISIBLE);
		return;
	}

	// Progressbar verbergen
	private void hideProgressBar()
	{
		View mProgressbar = findViewById(R.id.toolbar_progress_bar_detailactivity);
		mProgressbar.setVisibility(View.GONE);
	}

	@Override
	protected void onStart()
	{
		super.onStart();

		Log.i("HermLog", "DetailActivity: onStart()");

		// Progressbar tonen als downloadproces nog loopt
		// na configuratie verandering
		if (taskFragment.isRunning())
		{
			showProgressBar();
		}

		// Alleen share button tonen als link beschikbaar is
		invalidateOptionsMenu();

		// Als er al data in de intent stonden,
		// Wordt DetailActivity aangeroepen uit lijst,
		// Zo niet, dan is deze Activity gestart uit een
		// Notificatie, dan data nog downloaden
		if (!weHaveData)
		{
			downloadXml();
		}
	}
}
