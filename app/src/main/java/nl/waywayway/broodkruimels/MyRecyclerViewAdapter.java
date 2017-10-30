package nl.waywayway.broodkruimels;

import android.content.*;
import android.support.v7.widget.*;
import android.text.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import com.squareup.picasso.*;
import java.util.*;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.CustomViewHolder>
{
    private List<FeedItem> feedItemList;
    private Context context;
    private OnItemClickListener onItemClickListener;
	private int columnWidth;
	private float logicalDensity;
	private String screenWidth;
	private int itemLayout;
	
    public MyRecyclerViewAdapter(Context context, List<FeedItem> feedItemList, int columnWidth, float logicalDensity, String screenWidth)
	{
        this.feedItemList = feedItemList;
        this.context = context;
		this.columnWidth = columnWidth;
		this.logicalDensity = logicalDensity;
		this.screenWidth = screenWidth;
	}

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
	{		
		// Inflate juiste layout voor smal of breed scherm
		// mScreenWidth wordt in MainActivity bepaald
		if (screenWidth == "narrow")
		{
			itemLayout = R.layout.recyclerview_item_listlayout;
		}
		else
		{
			itemLayout = R.layout.recyclerview_item_staggeredgridlayout;
		}

		View view = LayoutInflater.from(viewGroup.getContext()).inflate(itemLayout, null);
		CustomViewHolder viewHolder = new CustomViewHolder(view);
		
		return viewHolder;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder, int i)
	{
        final FeedItem feedItem = feedItemList.get(i);

        //Download image using picasso library
        if (!TextUtils.isEmpty(feedItem.getMediacontent()))
		{
			if (screenWidth == "narrow")
			{
				// Afbeelding voor lijst layout
				Picasso
					.with(context)
					.load(feedItem.getMediacontent())
					.error(R.drawable.placeholder)
					.placeholder(R.drawable.placeholder)
					.resize(80, 80)
					.centerCrop()
					.transform(new RoundedCornersTransformation(3, 0, RoundedCornersTransformation.CornerType.LEFT))
					.into(customViewHolder.imageView);
			}
			else
			{
				// kolom breedte staat in values in eenheid dp,
				// hier omrekenen in pixels
				int mImgWidthPixels = Math.round( columnWidth * logicalDensity ) + 1;
				
				// Log.i("HermLog", "mColumnWidth: " + mColumnWidth);
				// Log.i("HermLog", "mLogicalDensity: " + mLogicalDensity);
				// Log.i("HermLog", "mImgWidthPixels: " + mImgWidthPixels);
				
				// Afbeelding voor staggered grid layout
				Picasso
					.with(context)
					.load(feedItem.getMediacontent())
					.error(R.drawable.placeholder)
					.placeholder(R.drawable.placeholder)
					.resize(mImgWidthPixels, 0)
					.transform(new RoundedCornersTransformation(2, 0, RoundedCornersTransformation.CornerType.TOP))
					.into(customViewHolder.imageView);
			}
        }

        //Setting text views
        customViewHolder.textViewTitle.setText(Html.fromHtml(feedItem.getTitle()));
        customViewHolder.textViewPubdate.setText(Html.fromHtml(feedItem.getPubDate()));

        View.OnClickListener listener = new View.OnClickListener()
		{
            @Override
            public void onClick(View v)
			{
                onItemClickListener.onItemClick(feedItem);
            }
        };
		
        customViewHolder.imageView.setOnClickListener(listener);
        customViewHolder.textViewTitle.setOnClickListener(listener);
        customViewHolder.textViewPubdate.setOnClickListener(listener);
		customViewHolder.itemView.setOnClickListener(listener);
    }

    @Override
    public int getItemCount()
	{
        return (null != feedItemList ? feedItemList.size() : 0);
    }

    class CustomViewHolder extends RecyclerView.ViewHolder
	{
        protected ImageView imageView;
        protected TextView textViewTitle;
        protected TextView textViewPubdate;
		protected CardView cardView;
		
        public CustomViewHolder(View view)
		{
            super(view);
            this.imageView = (ImageView) view.findViewById(R.id.thumbnail);
            this.textViewTitle = (TextView) view.findViewById(R.id.title);
            this.textViewPubdate = (TextView) view.findViewById(R.id.pubdate);
			this.cardView = (CardView) view.findViewById(R.id.card);
        }
    }
	
    public OnItemClickListener getOnItemClickListener()
	{
        return onItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener)
	{
        this.onItemClickListener = onItemClickListener;
    }
}
