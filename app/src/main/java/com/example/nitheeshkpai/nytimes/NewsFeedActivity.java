package com.example.nitheeshkpai.nytimes;

import android.content.Intent;
import android.os.Bundle;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.nitheeshkpai.nytimes.info.NewsItemInfo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class NewsFeedActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, NavigationView.OnNavigationItemSelectedListener {

    private final String NEWS_JSON_KEY = "results";

    private SwipeRefreshLayout swipeRefreshLayout;

    private Gson gson;

    private RecyclerView recyclerView;
    private LinearLayoutManager mLayoutManager;
    private List<NewsItemInfo> newsItemsInfoList = new ArrayList<>();
    private final List<NewsItemInfo> displayList = new ArrayList<>();

    private int previousTotal = 0;
    private boolean loading = true;
    private final int visibleThreshold = 5;
    @SuppressWarnings("WeakerAccess")
    private
    int firstVisibleItem;
    @SuppressWarnings("WeakerAccess")
    private
    int visibleItemCount;
    @SuppressWarnings("WeakerAccess")
    private
    int totalItemCount;

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

        init();

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

    private void init() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
    }

    @Override
    protected void onResume() {
        super.onResume();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                visibleItemCount = recyclerView.getChildCount();
                totalItemCount = mLayoutManager.getItemCount();
                firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition();

                if (loading) {
                    if (totalItemCount > previousTotal) {
                        loading = false;
                        previousTotal = totalItemCount;
                    }
                }
                if (!loading && (totalItemCount - visibleItemCount)
                        <= (firstVisibleItem + visibleThreshold)) {

                    loadForDisplay();

                    loading = true;
                }
            }
        });
    }

    private void makeNetworkRequest() {

        GsonBuilder gsonBuilder = new GsonBuilder().serializeNulls();
        gson = gsonBuilder.create();

        RequestQueue queue = Volley.newRequestQueue(this);

        String REQUEST_URL = "https://api.nytimes.com/svc/mostpopular/v2/mostviewed/all-sections/1.json?";
        String API_KEY = "api_key=6e766524b9f94c7b9910b09198659fe9";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, REQUEST_URL + API_KEY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONArray newsItemsListJSONArray = null;
                        try {
                            JSONObject jsonObj = new JSONObject(response);
                            newsItemsListJSONArray = jsonObj.getJSONArray(NEWS_JSON_KEY);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        String syntaxCorrectedJSONString = handleJSONSyntax(newsItemsListJSONArray);

                        Type type = new TypeToken<List<NewsItemInfo>>() {
                        }.getType();
                        try {
                            newsItemsInfoList = gson.fromJson(syntaxCorrectedJSONString, type);
                        } catch (JsonSyntaxException e) {
                            Toast.makeText(NewsFeedActivity.this, "Bad data from Server!", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }

                        if (newsItemsInfoList.size() != displayList.size()) {
                            updateUI();
                        }
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

    /*
     * Handle edge cases of the JSON parsing
     */
    private String handleJSONSyntax(JSONArray newsItemsListJSONArray) {
        String currentKeyTags = "\"media\":\"\"";
        String expectedKeyTags = "\"media\":[]";

        return newsItemsListJSONArray.toString()
                .replaceAll(currentKeyTags, expectedKeyTags);
    }

    private void updateUI() {
        loadForDisplay();
        NewsItemsAdapter adapter = new NewsItemsAdapter(this, displayList);
        recyclerView.setAdapter(adapter);
    }

    private void loadForDisplay() {
        for (int i = 0; i < 10; i++) {
            if (!newsItemsInfoList.isEmpty()) {
                displayList.add(newsItemsInfoList.get(0));
                newsItemsInfoList.remove(0);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                final Intent intent = new Intent(this, SearchActivity.class);
                startActivity(intent);
        }
        return true;
    }

    @Override
    public void onRefresh() {
        makeNetworkRequest();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.news_feed_options_menu, menu);
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.saved_items:
                Intent intent = new Intent(this, SavedNewsItemsActivity.class);
                startActivity(intent);
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
