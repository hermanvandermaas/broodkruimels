package nl.waywayway.broodkruimels;

import android.content.*;
import android.net.*;
import android.os.*;
import android.support.v7.app.*;
import android.text.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import com.squareup.picasso.*;
import com.github.chrisbanes.photoview.*;

public class ImageActivity extends AppCompatActivity
{
	private Intent mIntent;
	private String mImageUrl;
	private int mImgWidth;
	private int mImgHeight;
	private Context mContext;
	private PhotoView mImageview;
	private String mUrlDimensions;
	private int mUrlWidth;
	private int mUrlHeight;
	private Float mAspectRatio;

	@Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image);

		Log.i("HermLog", "ImageActivity.java");

		mImageview = (PhotoView) findViewById(R.id.image_big);
		
		// zet referentie naar context van deze activity in een variabele
		mContext = this;

		// Data uit intent halen
		getDataFromIntent();

		// Actie bij klik op knop probeer opnieuw
		setClickActionTryAgain();

		// Instellen:
		// bij klik op scherm fullscreen aan of uit zetten
		InitializeFullscreenToggle();
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
	}

	// Actie bij klik op knop probeer opnieuw:
	// check verbinding, indien ok dan afbeelding laden
	private void setClickActionTryAgain()
	{
		Button button = (Button) findViewById(R.id.btnTryAgain_image_activity);
        button.setOnClickListener(new View.OnClickListener()
			{
				public void onClick(View v)
				{
					downloadImageOrTryAgain();
				}
			});
	}

	// Instellen:
	// bij klik op scherm fullscreen aan of uit zetten
	private void InitializeFullscreenToggle()
	{
		PhotoViewAttacher mAttacher = new PhotoViewAttacher(mImageview);

		mAttacher.setOnPhotoTapListener(new OnPhotoTapListener()
			{
				public void onPhotoTap(ImageView view, float x, float y)
				{
					Toast.makeText(mContext, "Op de foto... Tik!", Toast.LENGTH_SHORT).show();
					toggleFullscreen();
				}
			});

		mAttacher.setOnOutsidePhotoTapListener(new OnOutsidePhotoTapListener()
			{
				public void onOutsidePhotoTap(ImageView view)
				{
					Toast.makeText(mContext, "Buiten de foto... Tik!", Toast.LENGTH_SHORT).show();
					toggleFullscreen();
				}
			});
	}

	private void toggleFullscreen()
	{
		
		
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus)
	{
        super.onWindowFocusChanged(hasFocus);
		if (hasFocus) setFullscreen();
	}

	// Fullscreen
	private void setFullscreen()
	{
		Log.i("HermLog", "setFullscreen()");
		Toast.makeText(mContext, "Build version SDK int: " + Build.VERSION.SDK_INT, Toast.LENGTH_SHORT).show();

		View mDecorView = this.getWindow().getDecorView();

		if (Build.VERSION.SDK_INT >= 19)
		{
			Toast.makeText(mContext, "setFullscreen() api>=19", Toast.LENGTH_SHORT).show();

			// Set the IMMERSIVE flag.
			// Set the content to appear under the system bars so that the content
			// doesn't resize when the system bars hide and show.
			mDecorView.setSystemUiVisibility(
				View.SYSTEM_UI_FLAG_LAYOUT_STABLE
				| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
				| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
				| View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
				| View.SYSTEM_UI_FLAG_IMMERSIVE);
		}
		else
		{
			Toast.makeText(mContext, "setFullscreen() api<19", Toast.LENGTH_SHORT).show();
			
			findViewById(android.R.id.content).setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
			getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
		}
	}

	// Verlaat fullscreen
	private void exitFullscreen()
	{
		Log.i("HermLog", "exitFullscreen()");

		View mDecorView = this.getWindow().getDecorView();

		if (Build.VERSION.SDK_INT >= 19)
		{
			Toast.makeText(mContext, "exitFullscreen() api>=19", Toast.LENGTH_SHORT).show();
			
			mDecorView.setSystemUiVisibility(
				View.SYSTEM_UI_FLAG_LAYOUT_STABLE
				| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
		}
		else
		{
			Toast.makeText(mContext, "exitFullscreen() api<19", Toast.LENGTH_SHORT).show();
			
			findViewById(android.R.id.content).setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
			getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
		}
	}

	// Controleer netwerkverbinding, toon knop
	// 'probeer opnieuw'  indien nodig
	private void downloadImageOrTryAgain()
	{
		Log.i("HermLog", "downloadImageOrTryAgain()");

		if (isNetworkConnected())
		{
			// Als gestart door knop probeer opnieuw,
			// verberg knop
			View mNotConnectedLayout = findViewById(R.id.notConnectedLinLayout_image_activity);
			mNotConnectedLayout.setVisibility(View.GONE);

			downloadImage(false);
		}
		else
		{
			Log.i("HermLog", "ImageActivity: geen verbinding");
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
	private void tryAgain(String mMsg)
	{	
		TextView txtview = (TextView) findViewById(R.id.txtTryAgain_image_activity);
		txtview.setText(mMsg);

		View viewTryAgain = findViewById(R.id.notConnectedLinLayout_image_activity);
		viewTryAgain.setVisibility(View.VISIBLE);

		Log.i("HermLog", "ImageActivity: tryAgain()");
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
		Log.i("HermLog", "2e poging: " + secondTry);
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
		// secondTry = true;

		// Poging 1
		if (!secondTry)
		{
			showProgressBar();

			// Laad verkleinde afbeelding
			Picasso
				.with(mContext)
				.load(makeUrl(secondTry))
				.into(mImageview, new Callback()
				{
					@Override
					public void onSuccess()
					{
						hideProgressBar();
					}

					@Override
					public void onError()
					{
						Log.i("HermLog", "1e poging: afbeelding downloadfout, start 2e poging...");
						downloadImage(true);
					}
				});				
		}
		else
		{
			showProgressBar();

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
						hideProgressBar();
					}

					@Override
					public void onError()
					{
						hideProgressBar();
						tryAgain(getResources().getString(R.string.txt_try_again_nodownload));

						Log.i("HermLog", "2e poging ook mislukt: afbeelding downloadfout");
					}
				});
		}
	}

	// Progressbar tonen
	private void showProgressBar()
	{
		View mProgressbar = findViewById(R.id.image_big_progress_bar);
		mProgressbar.setVisibility(View.VISIBLE);
		return;
	}

	// Progressbar verbergen
	private void hideProgressBar()
	{
		View mProgressbar = findViewById(R.id.image_big_progress_bar);
		mProgressbar.setVisibility(View.GONE);
	}


	/************************/
	/***** LOGS & STUFF *****/
	/************************/

	@Override
	protected void onStart()
	{
		super.onStart();
		Log.i("HermLog", "ImageActivity: onStart()");

		downloadImageOrTryAgain();
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		Log.i("HermLog", "ImageActivity: onResume()");
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		Log.i("HermLog", "ImageActivity: onPause()");
	}

	@Override
	protected void onStop()
	{
		super.onStop();
		Log.i("HermLog", "ImageActivity: onStop()");
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		Log.i("HermLog", "ImageActivity: onDestroy()");
	}
}
