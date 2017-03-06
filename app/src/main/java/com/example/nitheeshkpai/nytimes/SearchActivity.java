package com.example.nitheeshkpai.nytimes;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.nitheeshkpai.nytimes.info.NewsItemInfo;
import com.example.nitheeshkpai.nytimes.info.SearchResultItemInfo;
import com.example.nitheeshkpai.nytimes.utils.Constants;
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

/**
 * Created by nitheeshkpai on 3/4/17.
 * Activity that runs the article search
 */

public class SearchActivity extends AppCompatActivity {

    private static final String DOCS_JSON_KEY = "docs";
    private static final String RESPONSE_JSON_KEY = "response";

    private Gson gson;

    private List<SearchResultItemInfo> searchResultItemsInfoList = new ArrayList<>();
    private final List<NewsItemInfo> displayList = new ArrayList<>();

    private NewsItemsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        initView();
    }

    private void initView() {
        EditText searchText = (EditText) findViewById(R.id.search_text);
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                displayList.clear();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                runWebSearch(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        adapter = new NewsItemsAdapter(SearchActivity.this, displayList);
        recyclerView.setAdapter(adapter);

        ItemTouchHelper.SimpleCallback swipeActions = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                switch (swipeDir) {
                    case ItemTouchHelper.LEFT :
                        if(adapter.saveNewsItem(displayList.get(viewHolder.getAdapterPosition()))){
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
    }

    private void runWebSearch(final String query) {

        if (query.isEmpty()) {
            displayList.clear();
            adapter.notifyDataSetChanged();
            return;
        }
        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.SEARCH_REQUEST_URL + Constants.USER_API_KEY + "&q=" + query,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONArray searchResultItemsJSONArray = null;
                        try {
                            JSONObject jsonObj = new JSONObject(response);
                            searchResultItemsJSONArray = jsonObj.getJSONObject(RESPONSE_JSON_KEY).getJSONArray(DOCS_JSON_KEY);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Type type = new TypeToken<List<SearchResultItemInfo>>() {
                        }.getType();

                        try {
                            searchResultItemsInfoList = gson.fromJson(searchResultItemsJSONArray != null ? searchResultItemsJSONArray.toString() : null, type);
                        } catch (JsonSyntaxException e) {
                            Toast.makeText(SearchActivity.this, "Bad data from Server!", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }

                        for (SearchResultItemInfo temp : searchResultItemsInfoList != null ? searchResultItemsInfoList : null) {
                            displayList.add(new NewsItemInfo(temp));
                        }
                        adapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        queue.add(stringRequest);
    }
}

