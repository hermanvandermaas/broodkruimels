package nl.waywayway.broodkruimels;

import android.app.*;
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
import java.text.*;
import java.util.*;
import org.json.*;

import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;

public class DetailActivity extends AppCompatActivity implements TaskFragment.TaskCallbacks
{
	private static final String TAG_TASK_FRAGMENT = "task_fragment";
	private Boolean mWeHaveData = false;
	private Intent mIntent;
	private String mImageUrl;
	private TaskFragment mTaskFragment;
	private int mImgWidth;
	private int mImgHeight;
	private String mTitle;
	private String mLink;
	private String mPubdate;
	private String mCreator;
	private String mContent;
	private Context mContext;
	private ImageView mImageview;
	private ProgressBar mProgressBar;
	private TextView mTextViewTitle;
	private TextView mTextViewPubdate;
	private TextView mTextViewCreator;
	private TextView mTextViewContent;
	private String mUrlDimensions;
	private int mUrlWidth;
	private int mUrlHeight;
	private Float mAspectRatio;

	@Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_detail);

		// zet referentie naar context van deze activity in een variabele
		mContext = this;

		// Maak toolbar
		makeToolBar();

		// Probeer data uit intent te halen
		// indien data aanwezig dan wordt deze activity gestart
		// vanuit de lijst en zijn data aanwezig;
		// Indien data niet aanwezig dan wordt deze activity blijkbaar
		// gestart vanuit een notification en moeten data nog
		// worden downgeload
		getDataFromIntent();
		if (!TextUtils.isEmpty(mImageUrl))
			mWeHaveData = true;

		Log.i("HermLog", "mWeHaveData: " + mWeHaveData);
		
		if (mWeHaveData)
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
		mTaskFragment = (TaskFragment) fm.findFragmentByTag(TAG_TASK_FRAGMENT);

		// If the Fragment is non-null, then it is being retained
		// over a configuration change.
		if (mTaskFragment == null)
		{
			mTaskFragment = new TaskFragment();
			fm.beginTransaction().add(mTaskFragment, TAG_TASK_FRAGMENT).commit();
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
		if (!TextUtils.isEmpty(mLink))
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
			.setText(mLink)
			.setSubject(mTitle)
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

		mIntent = getIntent();
		mImageUrl = mIntent.getStringExtra("mediacontent");
		// mImgWidth en mImgHeight zijn afmetingen van de oorspronkelijke niet verkleinde afbeelding
		mImgWidth = mIntent.getIntExtra("imgwidth", 1);
		mImgHeight = mIntent.getIntExtra("imgheight", 1);
		mTitle = mIntent.getStringExtra("title");
		mLink = mIntent.getStringExtra("link");
		mPubdate = mIntent.getStringExtra("pubdate");
		mCreator = mIntent.getStringExtra("creator");
		mContent = mIntent.getStringExtra("content");
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
			if (!mTaskFragment.isRunning())
			{
				// Bij eerste download van items start(false)
				// bij latere download van extra items (niet aan de
				// orde in deze class) start(true)
				mTaskFragment.start(false);
			}
			else
			{
				Log.i("HermLog", "DetailActivity: mTaskFragment.isRunning(): " + mTaskFragment.isRunning());
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
	// zet 	hasDownloaded flag op false
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

		if (mTaskFragment.isRunning())
		{
			mTaskFragment.cancel();
		}
	}

	private String makeUrl(Boolean secondTry)
	{
		// Maak juiste URL voor downloaden grote afbeelding
		Boolean mSizeKnown = (mImgWidth > 0 && mImgHeight > 0);
		if (secondTry) mSizeKnown = false;
		String mOrientation = (mImgWidth > mImgHeight) ? ("landscape") : ("portrait");
		mUrlWidth = getResources().getInteger(R.integer.activity_detail_image_size);

		// Maak deel van URL met afmetingen van afbeelding
		mAspectRatio = (float) mImgHeight / mImgWidth;
		mUrlHeight = Math.round(mUrlWidth * mAspectRatio);
		mUrlDimensions = "-" + String.valueOf(mUrlWidth) + "x" + String.valueOf(mUrlHeight);

		/*
		 if (mOrientation == "landscape" && mSizeKnown)
		 {
		 mAspectRatio = (float) mImgHeight / mImgWidth;
		 mUrlHeight = Math.round(mUrlWidth * mAspectRatio);
		 mUrlDimensions = "-" + String.valueOf(mUrlWidth) + "x" + String.valueOf(mUrlHeight);
		 }

		 if (mOrientation == "portrait" && mSizeKnown)
		 {
		 mUrlHeight = mUrlWidth;
		 mAspectRatio = (float) mImgWidth / mImgHeight;
		 mUrlWidth = Math.round(mUrlHeight * mAspectRatio);
		 mUrlDimensions = "-" + String.valueOf(mUrlWidth) + "x" + String.valueOf(mUrlHeight);
		 }
		 */

		if (!mSizeKnown)
		{
			// Als afmetingen niet bekend, of als
			// dit tweede poging is voor downloaden afbeelding,
			// oorspronkelijke afbeelding downloaden,
			// mogelijk erg grote afbeelding
			mUrlDimensions = "";
		}

		String mRegex = "(?i)(.+)(-\\d+x\\d+)(\\.jpg|\\.jpeg|\\.png)";
		mImageUrl = mImageUrl.replaceAll(mRegex, "$1" + mUrlDimensions + "$3");

		/*
		 Log.i("HermLog", "mOrientation: " + mOrientation);
		 Log.i("HermLog", "2e poging: " + secondTry);
		 Log.i("HermLog", "mSizeknown: " + mSizeKnown);
		 Log.i("HermLog", "mUrlWidth: " + mUrlWidth);
		 Log.i("HermLog", "mUrlHeight: " + mUrlHeight);
		 Log.i("HermLog", "mAspectratio: " + mAspectRatio);			
		 Log.i("HermLog", "mImgWidth: " + mImgWidth);
		 Log.i("HermLog", "mImgHeight: " + mImgHeight);
		 Log.i("HermLog", "mImageUrl: " + mImageUrl);
		 */

		return mImageUrl;
	}

	// Download image using picasso library
	// parameter secondTry geeft aan of eerste download mislukt is,
	// zo ja, dan oorspronkelijke (mogelijk erg grote)
	// afbeelding proberen te downloaden
	private void downloadImage(Boolean secondTry)
	{
		mImageview = (ImageView) findViewById(R.id.image_detail);

		// Poging 1
		if (!secondTry)
		{
			// progress bar indeterminate (draaiende cirkel)
			// zichtbaar maken tijdens downloaden afbeelding
			mProgressBar = (ProgressBar) findViewById(R.id.image_detail_progress_bar);
			mProgressBar.setVisibility(ProgressBar.VISIBLE);

			// Laad grote afbeelding
			// maar wel verkleind
			Picasso
				.with(mContext)
				.load(makeUrl(secondTry))
				.into(mImageview, new Callback()
				{
					@Override
					public void onSuccess()
					{
						mProgressBar.setVisibility(ProgressBar.GONE);
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
			// Laad grote afbeelding
			// onverkleind
			Picasso
				.with(mContext)
				.load(makeUrl(secondTry))
				.resize(mUrlWidth, 0)
				.into(mImageview, new Callback()
				{
					@Override
					public void onSuccess()
					{
						mProgressBar.setVisibility(ProgressBar.GONE);
					}

					@Override
					public void onError()
					{
						mProgressBar.setVisibility(ProgressBar.GONE);
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
					Intent mImageIntent = new Intent(mContext, ImageActivity.class);

					mImageIntent.putExtra("mediacontent", mImageUrl);
					mImageIntent.putExtra("imgwidth", mImgWidth);
					mImageIntent.putExtra("imgheight", mImgHeight);

					mContext.startActivity(mImageIntent);
				}
			});
	}

	private void setText()
	{
		// Vind text views
		// title-pubdate-creator-content
		mTextViewTitle = (TextView) findViewById(R.id.title_detail);
		mTextViewPubdate = (TextView) findViewById(R.id.pubdate_detail);
		mTextViewCreator = (TextView) findViewById(R.id.creator_detail);
		mTextViewContent = (TextView) findViewById(R.id.content_detail);

		// Setting text views
		mTextViewTitle.setText(Html.fromHtml(mTitle));
		mTextViewPubdate.setText(Html.fromHtml(mPubdate));
		mTextViewCreator.setText(Html.fromHtml(mCreator));
		mTextViewContent.setText(Html.fromHtml(mContent));
		// Maak links klikbaar
		mTextViewContent.setMovementMethod(LinkMovementMethod.getInstance());

		/*
		 TextView tv = (TextView) findViewById(R.id.title_detail);
		 float ts = tv.getTextSize();
		 Toast.makeText(mContext, String.valueOf(ts), Toast.LENGTH_SHORT).show();
		 */
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
	public void onPostExecute(String mResult, Boolean downloadMoreItems)
	{
		Log.i("HermLog", "DetailActivity: onPostExecute()");

		// Verberg progress bar
		hideProgressBar();

		// Als niets gedownload, toon boodschap
		// en knop probeer opnieuw
		if (mResult == "Fout!")
		{
			Log.i("HermLog", "DetailActivity: Niets gedownload");
			tryAgain(getResources().getString(R.string.txt_try_again_nodownload));
			return;
		}		

		// Als download blijkbaar goed is gegaan
		// resultaat parsen in een arraylist
		parseResult(mResult, downloadMoreItems);

		if (!TextUtils.isEmpty(mImageUrl))
		{
			mWeHaveData = true;

			// Data zijn beschikbaar, toon share knop
			// deze method roept onCreateOptionsMenu() aan
			invalidateOptionsMenu();
		}

		// Als data aanwezig zijn in de lijst,
		// update de UI met de data
		if (!TextUtils.isEmpty(mImageUrl))
		{
			Log.i("HermLog", "DetailActivity: data gedownload, update UI");

			// Download afbeelding
			downloadImage(false);

			// Zet tekst in textviews
			setText();
		}
	}

	// json string verwerken na download
	// Zet json data in variabelen
	private void parseResult(String result, Boolean downloadMoreItems)
	{
		Log.i("HermLog", "DetailActivity: parseResult()");

        try
		{
            JSONObject response = new JSONObject(result);
            JSONArray posts = response.optJSONArray("data");
			JSONObject post = posts.optJSONObject(0);

			mImageUrl = post.optString("mediacontent");
			// mImgWidth en mImgHeight zijn afmetingen van de oorspronkelijke niet verkleinde afbeelding
			mImgWidth = post.optInt("imgwidth");
			mImgHeight = post.optInt("imgheight");
			mTitle = post.optString("title");
			mLink = post.optString("link");
			mPubdate = formatDate(post.optString("pubDate"), "yyyy-MM-dd HH:mm:ss");
			mCreator = post.optString("creator");
			mContent = post.optString("content");
        }
		catch (JSONException e)
		{
			Log.i("HermLog", "DetailActivity: JSON Exception in parseResult");
            e.printStackTrace();
        }
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
			Log.i("HermLog", "DetailActivity: Date format exception in parseResult");
			e.printStackTrace();
		}

		return "";
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


	/************************/
	/***** LOGS & STUFF *****/
	/************************/

	@Override
	protected void onStart()
	{
		super.onStart();

		Log.i("HermLog", "DetailActivity: onStart()");

		// Progressbar tonen als downloadproces nog loopt
		// na configuratie verandering
		if (mTaskFragment.isRunning())
		{
			showProgressBar();
		}

		// Alleen share button tonen als link beschikbaar is
		invalidateOptionsMenu();

		// Als er al data in de intent stonden,
		// Wordt DetailActivity aangeroepen uit lijst,
		// Zo niet, dan is deze Activity gestart uit een
		// Notificatie, dan data nog downloaden
		if (!mWeHaveData)
		{
			downloadXml();
		}
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		Log.i("HermLog", "DetailActivity: onResume()");
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		Log.i("HermLog", "DetailActivity: onPause()");
	}

	@Override
	protected void onStop()
	{
		super.onStop();
		Log.i("HermLog", "DetailActivity: onStop()");
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		Log.i("HermLog", "DetailActivity: onDestroy()");
	}
}
