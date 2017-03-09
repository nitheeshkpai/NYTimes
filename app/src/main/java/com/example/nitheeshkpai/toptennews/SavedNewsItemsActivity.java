package com.example.nitheeshkpai.toptennews;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.nitheeshkpai.toptennews.info.NewsItemInfo;
import com.example.nitheeshkpai.toptennews.utils.DatabaseHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nitheeshkpai on 3/6/17.
 * Activity to take care of archived news articles
 */
public class SavedNewsItemsActivity extends AppCompatActivity {

    private DatabaseHandler dBHandler;

    private NewsItemsAdapter adapter;
    private List<NewsItemInfo> newsItemsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_saved_news_items);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        dBHandler = new DatabaseHandler(this);
        newsItemsList = dBHandler.getAllItems();
        adapter = new NewsItemsAdapter(this, newsItemsList);

        if (newsItemsList.isEmpty()) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage(this.getResources().getString(R.string.dialog_message_no_bookmarks))
                    .setTitle(this.getResources().getString(R.string.no_bookmarks))
                    .setPositiveButton("OK", null);
            AlertDialog dialog = alertDialogBuilder.create();
            dialog.show();
        }

        initView();
    }

    private void initView() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);

        ItemTouchHelper.SimpleCallback swipeActions = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                switch (swipeDir) {
                    case ItemTouchHelper.RIGHT:
                        adapter.deleteNewsItem(newsItemsList.get(viewHolder.getAdapterPosition()));
                        adapter.notifyDataSetChanged();
                        Toast.makeText(SavedNewsItemsActivity.this, SavedNewsItemsActivity.this.getResources().getString(R.string.removed_bookmark), Toast.LENGTH_SHORT).show();
                        invalidateOptionsMenu();
                        break;
                }
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeActions);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        Toast.makeText(this, this.getResources().getString(R.string.swipe_right_to_delete), Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.saved_items_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (newsItemsList.isEmpty()) {
            menu.findItem(R.id.remove_all).setEnabled(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.remove_all:
                dBHandler.deleteAllItems();
                newsItemsList.clear();
                adapter.notifyDataSetChanged();
                Toast.makeText(this, this.getResources().getString(R.string.removed_bookmark), Toast.LENGTH_SHORT).show();
                invalidateOptionsMenu();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}