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
		String mTitle = mIntent.getStringExtra("title");
		String mPubdate = mIntent.getStringExtra("pubdate");
		String mCreator = mIntent.getStringExtra("creator");
		String mContent = mIntent.getStringExtra("content");
		
		// Download image using picasso library
        if ( !TextUtils.isEmpty(mImageUrl) )
		{
			mImageview = (ImageView) findViewById(R.id.image_detail);
			
			// Maak juiste URL voor downloaden grote afbeelding
			String mRegex = "(.+)(-\\d+x\\d+)(.jpg)";
			// String mImageSize = getResources().getString(R.string.activity_detail_image_size);
			mImageUrl = mImageUrl.replaceAll(mRegex, "$1$3");
			
			Log.i("HermLog", "mImageUrl: " + mImageUrl);
			
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
