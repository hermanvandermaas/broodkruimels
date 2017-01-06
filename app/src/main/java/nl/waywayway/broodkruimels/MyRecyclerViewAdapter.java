package nl.waywayway.broodkruimels;

import android.*;
import android.content.*;
import android.support.v7.widget.*;
import android.text.*;
import android.view.*;
import android.widget.*;
import com.squareup.picasso.*;
import java.util.*;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.CustomViewHolder>
{
    private List<FeedItem> feedItemList;
    private Context mContext;
    private OnItemClickListener onItemClickListener;

    public MyRecyclerViewAdapter(Context context, List<FeedItem> feedItemList)
	{
        this.feedItemList = feedItemList;
        this.mContext = context;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
	{
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_row, null);
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
            Picasso
				.with(mContext)
				.load(feedItem.getMediacontent())
				.error(R.drawable.placeholder)
				.placeholder(R.drawable.placeholder)
				.into(customViewHolder.imageView);
        }

        //Setting text view title
        customViewHolder.textView.setText(Html.fromHtml(feedItem.getTitle()));


        View.OnClickListener listener = new View.OnClickListener()
		{
            @Override
            public void onClick(View v)
			{
                onItemClickListener.onItemClick(feedItem);
            }
        };

		
        customViewHolder.imageView.setOnClickListener(listener);
        customViewHolder.textView.setOnClickListener(listener);
    }

    @Override
    public int getItemCount()
	{
        return (null != feedItemList ? feedItemList.size() : 0);
    }


    class CustomViewHolder extends RecyclerView.ViewHolder
	{
        protected ImageView imageView;
        protected TextView textView;

        public CustomViewHolder(View view)
		{
            super(view);
            this.imageView = (ImageView) view.findViewById(R.id.thumbnail);
            this.textView = (TextView) view.findViewById(R.id.title);
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
