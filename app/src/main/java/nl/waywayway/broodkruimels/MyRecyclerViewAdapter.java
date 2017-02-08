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
    private Context mContext;
    private OnItemClickListener onItemClickListener;
	private String mScreenWidth;
	private int mItemLayout;

    public MyRecyclerViewAdapter(Context context, List<FeedItem> feedItemList)
	{
        this.feedItemList = feedItemList;
        this.mContext = context;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
	{		
		// Inflate juiste layout voor smal of breed scherm
		// mScreenWidth wordt in MainActivity bepaald op deze adapter class met een setter functie
		if (mScreenWidth == "narrow")
		{
			mItemLayout = R.layout.recyclerview_item_listlayout;
		}
		else
		{
			mItemLayout = R.layout.recyclerview_item_staggeredgridlayout;
		}

		View view = LayoutInflater.from(viewGroup.getContext()).inflate(mItemLayout, null);
		CustomViewHolder viewHolder = new CustomViewHolder(view);
		return viewHolder;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder, int i)
	{
        final FeedItem feedItem = feedItemList.get(i);
		final CustomViewHolder mCustomViewHolder = customViewHolder;

        //Download image using picasso library
        if (!TextUtils.isEmpty(feedItem.getMediacontent()))
		{
			if (mScreenWidth == "narrow")
			{
				// Afbeelding voor lijst layout
				Picasso
					.with(mContext)
					.load(feedItem.getMediacontent())
					.error(R.drawable.placeholder)
					.placeholder(R.drawable.placeholder)
					.resize(80, 80)
					.centerCrop()
					.transform(new RoundedCornersTransformation(2, 0, RoundedCornersTransformation.CornerType.LEFT))
					.into(customViewHolder.imageView);
			}
			else
			{
				// Vind breedte van de cards in een staggered grid layout
				// als die layout van toepassing is

				customViewHolder.itemView.post(new Runnable()
					{
						@Override
						public void run()
						{
							int imageWidth = feedItem.getMediawidth();
							int imageHeight = feedItem.getMediaheight();
							int cellWidth = mCustomViewHolder.itemView.getWidth();

							Log.i("HermLog", "cellWidth: " + cellWidth);

						}
					});

				// Afbeelding voor staggered grid layout
				Picasso
					.with(mContext)
					.load(feedItem.getMediacontent())
					.error(R.drawable.placeholder)
					.placeholder(R.drawable.placeholder)
					.transform(new RoundedCornersTransformation(2, 0, RoundedCornersTransformation.CornerType.TOP))
					.into(customViewHolder.imageView);
			}
        }

        //Setting text views
        customViewHolder.textViewTitle.setText(Html.fromHtml(feedItem.getTitle()));
        customViewHolder.textViewPubdate.setText(Html.fromHtml(feedItem.getPubdate()));
		customViewHolder.textViewContent.setText(Html.fromHtml(feedItem.getContent()));

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
		customViewHolder.textViewContent.setOnClickListener(listener);
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
		protected TextView textViewContent;

        public CustomViewHolder(View view)
		{
            super(view);
            this.imageView = (ImageView) view.findViewById(R.id.thumbnail);
            this.textViewTitle = (TextView) view.findViewById(R.id.title);
            this.textViewPubdate = (TextView) view.findViewById(R.id.pubdate);
			this.textViewContent = (TextView) view.findViewById(R.id.content);
        }
    }

	public void setScreenWidth(String mScreenwidth)
	{
		this.mScreenWidth = mScreenwidth;
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
