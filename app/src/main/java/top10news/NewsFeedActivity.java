package top10news;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import top10news.info.NewsItemInfo;
import top10news.utils.Constants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.techietakalabs.android.top10news.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class NewsFeedActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, NavigationView.OnNavigationItemSelectedListener {

    private final String NEWS_JSON_KEY = "articles";

    private SwipeRefreshLayout swipeRefreshLayout;
    private ImageView sourceLogoImageView;

    private Gson gson;

    private NewsItemsAdapter adapter;
    private List<NewsItemInfo> newsItemsInfoList = new ArrayList<>();
    private final List<NewsItemInfo> displayList = new ArrayList<>();

    private String newsSource;
    private String sourceLogo;

    private SharedPreferences sourcePreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_news_feed);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        //noinspection deprecation
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        initView();

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                makeNetworkRequest();
            }
        });
    }

    private void initView() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        adapter = new NewsItemsAdapter(this, displayList);
        recyclerView.setAdapter(adapter);

        ItemTouchHelper.SimpleCallback swipeActions = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                switch (swipeDir) {
                    case ItemTouchHelper.LEFT:
                        if (adapter.saveNewsItem(displayList.get(viewHolder.getAdapterPosition()))) {
                            displayList.remove(viewHolder.getAdapterPosition());
                        }
                        adapter.notifyDataSetChanged();
                        break;
                }
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeActions);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        Toast.makeText(this, this.getResources().getString(R.string.swipe_left_to_bookmark), Toast.LENGTH_SHORT).show();

        sourcePreference = PreferenceManager.getDefaultSharedPreferences(this);
        newsSource = sourcePreference.getString("sourcePref","bbc-news");
        sourceLogo = sourcePreference.getString("logoPref", "http://i.newsapi.org/bbc-news-s.png");

        sourceLogoImageView = (ImageView) findViewById(R.id.logo_image);
        Glide.with(this).load(sourceLogo).into(sourceLogoImageView);
        sourceLogoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewsFeedActivity.this, SelectFilterActivity.class);
                startActivityForResult(intent, 1);
            }
        });
    }

    private void makeNetworkRequest() {

        GsonBuilder gsonBuilder = new GsonBuilder().serializeNulls();
        gson = gsonBuilder.create();

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.MOST_VIEWED_REQUEST_URL + newsSource + Constants.VIEW_REQUEST_URL_SUFFIX + Constants.USER_API_KEY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONArray newsItemsListJSONArray = null;
                        newsItemsInfoList.clear();
                        try {
                            JSONObject jsonObj = new JSONObject(response);
                            newsItemsListJSONArray = jsonObj.getJSONArray(NEWS_JSON_KEY);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //String syntaxCorrectedJSONString = handleJSONSyntax(newsItemsListJSONArray);

                        Type type = new TypeToken<List<NewsItemInfo>>() {
                        }.getType();
                        try {
                            newsItemsInfoList = gson.fromJson(newsItemsListJSONArray.toString(), type);
                        } catch (JsonSyntaxException e) {
                            Toast.makeText(NewsFeedActivity.this, "Bad data from Server!", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }

                        updateUI();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(NewsFeedActivity.this);
                alertDialogBuilder.setMessage(NewsFeedActivity.this.getResources().getString(R.string.dialog_message_no_network))
                        .setTitle(NewsFeedActivity.this.getResources().getString(R.string.no_network));
                alertDialogBuilder.setPositiveButton("OK", null);
                AlertDialog dialog = alertDialogBuilder.create();
                dialog.show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        queue.add(stringRequest);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && data != null) {
            newsSource = data.getStringExtra("source");
            sourceLogo = data.getStringExtra("logo");

            Glide.with(this).load(sourceLogo).into(sourceLogoImageView);
            makeNetworkRequest();

            SharedPreferences.Editor editor = sourcePreference.edit();
            editor.putString("sourcePref", newsSource);
            editor.putString("logoPref", sourceLogo);
            editor.apply();
        }
    }

    private void updateUI() {
        displayList.clear();
        for (int i = 0; i < 10; i++) {
            if (!newsItemsInfoList.isEmpty()) {
                displayList.add(newsItemsInfoList.get(0));
                newsItemsInfoList.remove(0);
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onRefresh() {
        makeNetworkRequest();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.saved_items:
                Intent intent = new Intent(this, SavedNewsItemsActivity.class);
                startActivity(intent);
                break;
            case R.id.help :
                Intent intentHelp = new Intent(this, HelpActivity.class);
                startActivity(intentHelp);
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}