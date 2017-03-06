package com.example.nitheeshkpai.nytimes;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nitheeshkpai.nytimes.info.NewsItemInfo;
import com.example.nitheeshkpai.nytimes.utils.DatabaseHandler;
import com.squareup.picasso.Picasso;

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
        public final ImageView more;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            date = (TextView) view.findViewById(R.id.date);
            body = (TextView) view.findViewById(R.id.body);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            more = (ImageView) view.findViewById(R.id.more);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Intent intent = new Intent(mContext, WebViewActivity.class);
                    intent.putExtra(WebViewActivity.LINK_EXTRA, currentItem.getLink());
                    mContext.startActivity(intent);
                }
            });

            more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popup = new PopupMenu(mContext, v);
                    MenuInflater inflater = popup.getMenuInflater();
                    inflater.inflate(R.menu.card_overflow_popup_menu, popup.getMenu());

                    if (mContext instanceof SavedNewsItemsActivity) {
                        popup.getMenu().findItem(R.id.save).setVisible(false);
                        popup.getMenu().findItem(R.id.delete).setVisible(true);
                    }
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.share:
                                    shareNewsItem(currentItem);
                                    break;
                                case R.id.save:
                                    saveNewsItem(currentItem);
                                    break;
                                case R.id.delete:
                                    deleteNewsItem(currentItem);
                            }
                            return true;
                        }
                    });
                    popup.show();
                }
            });
        }
    }

    private void deleteNewsItem(NewsItemInfo currentItem) {
        dBHandler.deleteItem(currentItem);
        newsItemsInfoList.remove(currentItem);
        notifyDataSetChanged();
    }

    private void saveNewsItem(NewsItemInfo currentItem) {
        if (isAlreadySaved(currentItem)) {
            Toast.makeText(mContext, mContext.getResources().getString(R.string.already_bookmarked), Toast.LENGTH_SHORT).show();
            return;
        }
        dBHandler.addItem(currentItem);
        Toast.makeText(mContext, mContext.getResources().getString(R.string.added_bookmark), Toast.LENGTH_SHORT).show();
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
        Picasso.with(mContext).load(holder.currentItem.getImageURL()).placeholder(mContext.getResources().getDrawable(R.mipmap.ny_times_default_image)).into(holder.thumbnail);
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
