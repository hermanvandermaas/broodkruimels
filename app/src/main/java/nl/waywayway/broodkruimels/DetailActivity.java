package nl.waywayway.broodkruimels;

import android.content.*;
import android.os.*;
import android.support.v7.app.*;
import android.support.v7.widget.*;
import android.text.*;
import android.text.method.*;
import android.util.*;
import android.widget.*;
import com.squareup.picasso.*;

import android.support.v7.widget.Toolbar;

public class DetailActivity extends AppCompatActivity
{
	private Boolean mWeHaveData = false;
	private Intent mIntent;
	private String mImageUrl;
	private int mImgWidth;
	private int mImgHeight;
	private String mTitle;
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
		// Indien data niet aanwezig dan wordt deze activity
		// gestart vanuit een notification en moeten data nog
		// worden downgeload
		getDataFromIntent();
		if (!TextUtils.isEmpty(mImageUrl))
			mWeHaveData = true;

		Log.i("HermLog", "mWeHaveData: " + mWeHaveData);

		// Download afbeelding
		downloadImage(false);

		// Zet tekst in textviews
		setText();
    }

	private void makeToolBar()
	{
		// Maak toolbar
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_detail);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
		mPubdate = mIntent.getStringExtra("pubdate");
		mCreator = mIntent.getStringExtra("creator");
		mContent = mIntent.getStringExtra("content");
	}

	private String makeUrl(Boolean secondTry)
	{
		// Maak juiste URL voor downloaden grote afbeelding
		Boolean mSizeKnown = (mImgWidth > 0 && mImgHeight > 0);
		if (secondTry) mSizeKnown = false;
		String mOrientation = (mImgWidth > mImgHeight) ? ("landscape") : ("portrait");
		mUrlWidth = getResources().getInteger(R.integer.activity_detail_image_size);

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

		Log.i("HermLog", "mOrientation: " + mOrientation);
		Log.i("HermLog", "mSizeknown: " + mSizeKnown);
		Log.i("HermLog", "mUrlWidth: " + mUrlWidth);
		Log.i("HermLog", "mUrlHeight: " + mUrlHeight);
		Log.i("HermLog", "mAspectratio: " + mAspectRatio);			
		Log.i("HermLog", "mImgWidth: " + mImgWidth);
		Log.i("HermLog", "mImgHeight: " + mImgHeight);
		Log.i("HermLog", "mImageUrl: " + mImageUrl);

		return mImageUrl;
	}

	// Download image using picasso library
	// parameter secondTry geeft aan of eerste download mislukt is,
	// zo ja, dan oorspronkelijke (mogelijk erg grote)
	// afbeelding proberen te downloaden
	private void downloadImage(Boolean secondTry)
	{
		if (mWeHaveData)
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
	}

	private void setText()
	{
		// Vind text views
		// title-pubdate-creator-content
		mTextViewTitle = (TextView) findViewById(R.id.title_detail);
		mTextViewPubdate = (TextView) findViewById(R.id.pubdate_detail);
		mTextViewCreator = (TextView) findViewById(R.id.creator_detail);
		mTextViewContent = (TextView) findViewById(R.id.content_detail);

		if (mWeHaveData)
		{
			// Setting text views
			mTextViewTitle.setText(Html.fromHtml(mTitle));
			mTextViewPubdate.setText(Html.fromHtml(mPubdate));
			mTextViewCreator.setText(Html.fromHtml(mCreator));
			mTextViewContent.setText(Html.fromHtml(mContent));
			// Maak links klikbaar
			mTextViewContent.setMovementMethod(LinkMovementMethod.getInstance());
		}
	}
}
