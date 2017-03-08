package com.example.nitheeshkpai.nytimes;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.nitheeshkpai.nytimes.info.NewsItemInfo;
import com.example.nitheeshkpai.nytimes.utils.DatabaseHandler;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by nitheeshkpai on 3/3/17.
 * Recycler View adapter that handles the cardView in multiple places in the application
 */
public class NewsItemsAdapter extends RecyclerView.Adapter<NewsItemsAdapter.MyViewHolder> {

    private final Context mContext;
    private final List<NewsItemInfo> newsItemsInfoList;

    private final DatabaseHandler dBHandler;

    public NewsItemsAdapter(Context context, List<NewsItemInfo> newsItemsInfoList) {
        this.mContext = context;
        this.newsItemsInfoList = newsItemsInfoList;
        dBHandler = new DatabaseHandler(mContext);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public NewsItemInfo currentItem;
        public final TextView title;
        public final TextView date;
        public final TextView body;
        public final ImageView thumbnail;
        public final ImageView share;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            date = (TextView) view.findViewById(R.id.date);
            body = (TextView) view.findViewById(R.id.body);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            share = (ImageView) view.findViewById(R.id.share);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Intent intent = new Intent(mContext, WebViewActivity.class);
                    intent.putExtra(WebViewActivity.LINK_EXTRA, currentItem.getLink());
                    mContext.startActivity(intent);
                }
            });

            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shareNewsItem(currentItem);
                }
            });
        }
    }

    public void deleteNewsItem(NewsItemInfo currentItem) {
        dBHandler.deleteItem(currentItem);
        newsItemsInfoList.remove(currentItem);
        notifyDataSetChanged();
    }

    public boolean saveNewsItem(NewsItemInfo currentItem) {
        if (isAlreadySaved(currentItem)) {
            Toast.makeText(mContext, mContext.getResources().getString(R.string.already_bookmarked), Toast.LENGTH_SHORT).show();
            return false;
        }
        dBHandler.addItem(currentItem);
        Toast.makeText(mContext, mContext.getResources().getString(R.string.added_bookmark), Toast.LENGTH_SHORT).show();
        return true;
    }

    private void shareNewsItem(NewsItemInfo currentItem) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, currentItem.getLink());
        sendIntent.setType("text/plain");
        sendIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mContext.startActivity(sendIntent);
    }

    private boolean isAlreadySaved(NewsItemInfo item) {
        List<NewsItemInfo> dbList = dBHandler.getAllItems();

        for (NewsItemInfo i : dbList) {
            if (i.getLink().equals(item.getLink())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.news_card_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.currentItem = newsItemsInfoList.get(position);

        holder.title.setText(holder.currentItem.getTitle());
        holder.date.setText(formatDate(holder.currentItem.getDate()));
        holder.body.setText(holder.currentItem.getBody());

        //noinspection deprecation
        Picasso.with(mContext).load(holder.currentItem.getImageURL()).placeholder(new ColorDrawable(mContext.getResources().getColor(R.color.colorAccent))).into(holder.thumbnail);
    }

    private String formatDate(String dateString) {

        if (dateString == null) {
            return mContext.getResources().getString(R.string.default_date);
        }
        SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        Date date = null;
        try {
            date = form.parse(dateString);
        } catch (ParseException e) {

            e.printStackTrace();
        }

        SimpleDateFormat postFormatter = new SimpleDateFormat("dd MMM, yyyy", Locale.US);
        return postFormatter.format(date);
    }

    @Override
    public int getItemCount() {
        return newsItemsInfoList.size();
    }
}
