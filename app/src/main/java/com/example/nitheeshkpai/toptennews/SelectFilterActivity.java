package com.example.nitheeshkpai.toptennews;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.nitheeshkpai.toptennews.info.SourceItemInfo;
import com.example.nitheeshkpai.toptennews.utils.Constants;
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
 * Created by nitheeshkpai on 3/8/17.
 * Class that handles all DB stuff used in Save Article feature
 */
public class SelectFilterActivity extends AppCompatActivity {

    private final String SOURCES_JSON_KEY = "sources";

    private List<SourceItemInfo> sourceItemsInfoList = new ArrayList<>();

    private RecyclerView recyclerView;
    private GridLayoutManager mLayoutManager;
    private SourceItemsAdapter adapter;

    private Gson gson;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_filter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        initView();
        makeNetworkRequest();
    }

    private void initView() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_gridview);
        mLayoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(mLayoutManager);
    }

    private void makeNetworkRequest() {

        GsonBuilder gsonBuilder = new GsonBuilder().serializeNulls();
        gson = gsonBuilder.create();

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.SOURCES_LIST_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONArray sourceItemsListJSONArray = null;
                        try {
                            JSONObject jsonObj = new JSONObject(response);
                            sourceItemsListJSONArray = jsonObj.getJSONArray(SOURCES_JSON_KEY);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Type type = new TypeToken<List<SourceItemInfo>>() {
                        }.getType();
                        try {
                            sourceItemsInfoList = gson.fromJson(sourceItemsListJSONArray.toString(), type);
                        } catch (JsonSyntaxException e) {
                            Toast.makeText(SelectFilterActivity.this, "Bad data from Server!", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                        adapter = new SourceItemsAdapter(SelectFilterActivity.this, sourceItemsInfoList);
                        recyclerView.setAdapter(adapter);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SelectFilterActivity.this);
                alertDialogBuilder.setMessage(SelectFilterActivity.this.getResources().getString(R.string.dialog_message_no_network))
                        .setTitle(SelectFilterActivity.this.getResources().getString(R.string.no_network));
                alertDialogBuilder.setPositiveButton("OK", null);
                AlertDialog dialog = alertDialogBuilder.create();
                dialog.show();
            }
        });
        queue.add(stringRequest);
    }

}
