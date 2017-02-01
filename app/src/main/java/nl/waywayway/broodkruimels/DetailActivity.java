package nl.waywayway.broodkruimels;

import android.content.*;
import android.os.*;
import android.support.v7.app.*;
import android.support.v7.widget.*;
import android.text.*;
import android.util.*;
import android.widget.*;
import com.squareup.picasso.*;

import android.support.v7.widget.Toolbar;

public class DetailActivity extends AppCompatActivity
{
	private Context mContext;
	private ImageView mImageview;
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
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_detail);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		// Haal data uit intent
		Intent mIntent = getIntent();
		String mImageUrl = mIntent.getStringExtra("mediacontent");
		// mImgWidth en mImgHeight zijn afmetingen van de oorspronkelijke niet verkleinde afbeelding
		int mImgWidth = mIntent.getIntExtra("imgwidth", 1);
		int mImgHeight = mIntent.getIntExtra("imgheight", 1);
		String mTitle = mIntent.getStringExtra("title");
		String mPubdate = mIntent.getStringExtra("pubdate");
		String mCreator = mIntent.getStringExtra("creator");
		String mContent = mIntent.getStringExtra("content");
		
		// Download image using picasso library
        if ( !TextUtils.isEmpty(mImageUrl) )
		{
			mImageview = (ImageView) findViewById(R.id.image_detail);
			
			// Maak juiste URL voor downloaden grote afbeelding
			Boolean mSizeKnown = (mImgWidth > 0 && mImgHeight > 0);
			String mOrientation = (mImgWidth > mImgHeight) ? ("landscape") : ("portrait");
			String mImageSize = getResources().getString(R.string.activity_detail_image_size);
			
			if (mOrientation == "landscape" && mSizeKnown)
			{
				mUrlWidth = Integer.parseInt(mImageSize);
				mAspectRatio = (float) mImgHeight / mImgWidth;
				mUrlHeight = Math.round(mUrlWidth * mAspectRatio);
				mUrlDimensions = "-" + String.valueOf(mUrlWidth) + "x" + String.valueOf(mUrlHeight);
			}
			
			if (mOrientation == "portrait" && mSizeKnown)
			{
				mUrlHeight = Integer.parseInt(mImageSize);
				mAspectRatio = (float) mImgWidth / mImgHeight;
				mUrlWidth = Math.round(mUrlHeight * mAspectRatio);
				mUrlDimensions = "-" + String.valueOf(mUrlWidth) + "x" + String.valueOf(mUrlHeight);
			}
			
			if ( !mSizeKnown )
			{
				// Als afmetingen niet bekend, oorspronkelijke afbeelding downloaden
				// mogelijk erg grote afbeelding...
				mUrlDimensions = "";
			}
			
			String mRegex = "(?i)(.+)(-\\d+x\\d+)(\\.jpg|\\.jpeg)";
			mImageUrl = mImageUrl.replaceAll(mRegex, "$1" + mUrlDimensions + "$3");
			// eind maak url
			
			Log.i("HermLog", "mOrientation: " + mOrientation);
			Log.i("HermLog", "mSizeknown: " + mSizeKnown);
			Log.i("HermLog", "mUrlWidth: " + mUrlWidth);
			Log.i("HermLog", "mUrlHeight: " + mUrlHeight);
			Log.i("HermLog", "mAspectratio: " + mAspectRatio);			
			Log.i("HermLog", "mImgWidth: " + mImgWidth);
			Log.i("HermLog", "mImgHeight: " + mImgHeight);
			Log.i("HermLog", "mImageUrl: " + mImageUrl);
			
			// Laad grote afbeelding
            Picasso
				.with(mContext)
				.load(mImageUrl)
				.error(R.drawable.placeholder)
				.placeholder(R.drawable.placeholder)
				.into(mImageview);
        }

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
    }
}
