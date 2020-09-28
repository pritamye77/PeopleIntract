package com.example.peopleintract;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.peopleintract.adapter.RecyclerViewAdapter;
import com.example.peopleintract.db.ProfileContract.ProfileEntry;
import com.example.peopleintract.utilities.IndexItemNumber_RecyclerList;
import com.example.peopleintract.utilities.MySingleton;
import com.example.peopleintract.utilities.SyncTask;
import com.facebook.drawee.backends.pipeline.Fresco;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>, RecyclerViewAdapter.ProfileAdapterOnClickHandler{

    final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final int PROFILE_LOADER_ID = 11;
    Context mContext;
    RecyclerViewAdapter mRecyclerViewAdapter;
    RecyclerView mRecyclerView;
    List<Object> mRecyclerViewItems = new ArrayList<>();


    //Demo Data URL
    String API_URL = "https://randomuser.me/api/?results=10";

    ConstraintLayout welcomeLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        Fresco.initialize(this);

        mContext = this;
        mRecyclerView = findViewById(R.id.recyclerView);


        welcomeLayout = findViewById(R.id.welcomeLayout);
        welcomeLayout.setVisibility(View.VISIBLE);

        final LinearLayoutManager layoutManager =
                new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL,false);
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerViewAdapter = new RecyclerViewAdapter(mContext,this);
        mRecyclerView.setAdapter(mRecyclerViewAdapter);

        SnapHelper helper = new LinearSnapHelper();
        helper.attachToRecyclerView(mRecyclerView);

        DownloadProfileData();
    }

    public void DownloadProfileData(){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, API_URL, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        SyncTask.syncProfile(mContext, response);
                        DisplayProfile();


                    }}, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        DisplayProfile(); // already existing profile
                        Toast.makeText(MainActivity.this, "Network error", Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            public Map<String, String> getHeaders(){
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json;");
                return headers;
            }

        };
        MySingleton.getInstance(mContext).addToRequestQueue(jsonObjectRequest);
    }

    public void DisplayProfile(){
        getSupportLoaderManager().initLoader(PROFILE_LOADER_ID, null, this);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, @Nullable Bundle bundle) {

        switch (loaderId) {
            case PROFILE_LOADER_ID:

                Uri profileQueryUri = ProfileEntry.CONTENT_URI;
                String selection = ProfileEntry.getSqlSelectForAllProfile();

                return new CursorLoader(MainActivity.this,
                        profileQueryUri,
                        null,
                        null,
                        null,
                        null);


            default:
                throw new RuntimeException("Loader Not Implemented: "+ loaderId);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        Log.i(LOG_TAG, "onLoadFinished Started");
        Log.i(LOG_TAG, Integer.toString(data.getCount()));
        try{
            if (data.getCount() != 0) {

                Log.i(LOG_TAG, "data.getCount() != 0");

                for (int i = 0; i < data.getCount(); i++){
                    IndexItemNumber_RecyclerList itemNumberIndexForRecyclerList = new IndexItemNumber_RecyclerList(i);
                    mRecyclerViewItems.add(itemNumberIndexForRecyclerList);
                }

                mRecyclerViewAdapter.setRecyclerViewItems(mRecyclerViewItems);
                mRecyclerViewAdapter.swapCursor(data);
                mRecyclerViewAdapter.notifyDataSetChanged();

                showProfileDataView();


            }
        }catch (NullPointerException ne){
            Log.i(LOG_TAG,"data.getCount() is giving null");
            //TODO handle no sql data.
            mRecyclerView.setVisibility(View.GONE);

            Toast.makeText(mContext, "Network error. Couldn't load data.", Toast.LENGTH_LONG).show();
            ne.printStackTrace();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        Log.i(LOG_TAG,"loader reset ");
        mRecyclerViewAdapter.swapCursor(null);
        mRecyclerViewItems.clear();
    }

    @Override
    public void onClick(String profileUsername, int acceptanceId, int adapterPosition) {
        ContentValues cv = new ContentValues();

        cv.put(ProfileEntry.PROFILE_ACCEPTANCE_ID, Integer.toString(acceptanceId));

        String selections = ProfileEntry.PROFILE_USERNAME + " LIKE ?";
        String[] selectionArgs = {profileUsername};

        SyncTask.updateAcceptanceId(mContext, cv, selections, selectionArgs);
        mRecyclerViewAdapter.notifyItemChanged(adapterPosition);
    }

    public void showProfileDataView(){
        welcomeLayout.setVisibility(View.GONE);
    }
}
