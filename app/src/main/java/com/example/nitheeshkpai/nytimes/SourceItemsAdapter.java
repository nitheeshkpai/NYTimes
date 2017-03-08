package com.example.nitheeshkpai.nytimes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nitheeshkpai.nytimes.info.SourceItemInfo;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SourceItemsAdapter extends RecyclerView.Adapter<SourceItemsAdapter.ViewHolder> {

    private final Activity mActivity;
    private final List<SourceItemInfo> sourceItemsInfoList;

    public SourceItemsAdapter(Activity activity, List<SourceItemInfo> sourceItemsInfoList) {
        this.sourceItemsInfoList = sourceItemsInfoList;
        this.mActivity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.source_grid_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.currentItem = sourceItemsInfoList.get(position);
        Picasso.with(mActivity.getApplicationContext()).load(holder.currentItem.urlContainer.getLogoImageUrl()).placeholder(mActivity.getResources().getDrawable(R.mipmap.ny_times_default_image)).into(holder.logo);
    }

    @Override
    public int getItemCount() {
        return sourceItemsInfoList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public SourceItemInfo currentItem;
        private ImageView logo;

        public ViewHolder(View view) {
            super(view);
            logo = (ImageView) view.findViewById(R.id.source_logo);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent();
                    intent.putExtra("source",currentItem.getId());
                    mActivity.setResult(2,intent);
                    mActivity.finish();//finishing activity

                }
            });

            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    final Intent intent = new Intent(mActivity, WebViewActivity.class);
                    intent.putExtra(WebViewActivity.LINK_EXTRA, currentItem.getLink());
                    mActivity.startActivity(intent);
                    return false;
                }
            });
        }
    }

}