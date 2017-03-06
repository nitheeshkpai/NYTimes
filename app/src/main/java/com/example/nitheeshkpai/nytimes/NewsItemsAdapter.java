package com.example.nitheeshkpai.nytimes;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
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

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by nitheeshkpai on 3/3/17.
 */
public class NewsItemsAdapter extends RecyclerView.Adapter<NewsItemsAdapter.MyViewHolder> {

    private Context mContext;
    private List<NewsItemInfo> newsItemsInfoList;

    public NewsItemsAdapter(Context context, List<NewsItemInfo> newsItemsInfoList) {
        this.mContext = context;
        this.newsItemsInfoList = newsItemsInfoList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public NewsItemInfo currentItem;
        public TextView title;
        public TextView date;
        public TextView body;
        public ImageView thumbnail;
        public ImageView more;

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
                    intent.putExtra(WebViewActivity.LINK_EXTRA,currentItem.getLink());
                    mContext.startActivity(intent);
                }
            });

            more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popup = new PopupMenu(mContext,v);
                    MenuInflater inflater = popup.getMenuInflater();
                    inflater.inflate(R.menu.popup_menu, popup.getMenu());
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.share :
                                    shareNewsItem(currentItem);
                                    break;
                                case R.id.save :
                                    Toast.makeText(mContext,"Save",Toast.LENGTH_SHORT).show();
                                    break;
                            }
                            return true;
                        }
                    });
                    popup.show();
                }
            });
        }
    }

    private void shareNewsItem(NewsItemInfo currentItem) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, currentItem.getLink());
        sendIntent.setType("text/plain");
        mContext.startActivity(sendIntent);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.news_card, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.currentItem = newsItemsInfoList.get(position);

        holder.title.setText(holder.currentItem.getTitle());
        holder.date.setText(formatDate(holder.currentItem.getDate()));
        holder.body.setText(holder.currentItem.getBody());

        Picasso.with(mContext).load(holder.currentItem.getImageURL()).placeholder(new ColorDrawable(mContext.getResources().getColor(R.color.dark_grey))).into(holder.thumbnail);
    }

    private String formatDate(String dateString) {

        if(dateString == null) {
            return mContext.getResources().getString(R.string.default_date);
        }
        SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = form.parse(dateString);
        } catch (ParseException e) {

            e.printStackTrace();
        }

        SimpleDateFormat postFormater = new SimpleDateFormat("dd MMM, yyyy");
        return postFormater.format(date);
    }

    @Override
    public int getItemCount() {
        return newsItemsInfoList.size();
    }
}
