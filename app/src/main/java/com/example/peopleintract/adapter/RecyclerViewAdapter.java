package com.example.peopleintract.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.peopleintract.R;
import com.example.peopleintract.db.ProfileContract.ProfileEntry;
import com.example.peopleintract.utilities.IndexItemNumber_RecyclerList;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    private final Context mContext;
    private Cursor mCursor;
    private final String LOG_TAG = RecyclerViewAdapter.class.getSimpleName();

    private List<Object> mRecyclerViewItems;

    final private ProfileAdapterOnClickHandler mClickHandler;

    public interface  ProfileAdapterOnClickHandler{
        void onClick(String profileUsername, int acceptanceId, int adapterPosition);
    }

    public void swapCursor(Cursor newCursor) {
        Log.i(LOG_TAG,"swapped");
        mCursor = newCursor;
        Log.i(LOG_TAG, String.valueOf(mCursor.getCount()));

    }

    public RecyclerViewAdapter(Context context, ProfileAdapterOnClickHandler clickHandler) {
        mContext = context;
        mClickHandler=clickHandler;
    }

    public void setRecyclerViewItems(List<Object> recyclerViewItems){
        mRecyclerViewItems = recyclerViewItems;
    }

    public List<Object> getmRecyclerViewItems() {
        return mRecyclerViewItems;
    }

    public class ProfileItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final ImageView profileImageView;
        final TextView profileName;
        final TextView profileAge;
        final TextView profileGender;
        final TextView profileCity;
        final TextView acceptanceResult;
        final FloatingActionButton declineButton;
        final FloatingActionButton acceptButton;

        ProfileItemViewHolder(View view) {
            super(view);

            profileImageView = view.findViewById(R.id.profileImageView);
            profileName = view.findViewById(R.id.profile_name);
            profileAge = view.findViewById(R.id.ageTextView);
            profileGender = view.findViewById(R.id.gender);
            profileCity = view.findViewById(R.id.cityStateTextView);
            acceptanceResult = view.findViewById(R.id.acceptanceResultTextView);
            declineButton = view.findViewById(R.id.declineButton);
            acceptButton = view.findViewById(R.id.acceptButton);


            view.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {

        }
    }

    @Override
    public int getItemCount() {
        if (null == mCursor) return 0;
        //return mCursor.getCount();
        return mRecyclerViewItems.size();
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View profileItemLayoutView = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.match, viewGroup, false);
        profileItemLayoutView.setFocusable(true);
        return new ProfileItemViewHolder(profileItemLayoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        final ProfileItemViewHolder profileItemHolder = (ProfileItemViewHolder) holder;

        IndexItemNumber_RecyclerList itemNumberIndexForRecyclerList = (IndexItemNumber_RecyclerList) mRecyclerViewItems.get(position);
        mCursor.moveToPosition(itemNumberIndexForRecyclerList.getCursorPosition());


        String profileUsername = mCursor.getString(mCursor.getColumnIndex(ProfileEntry.PROFILE_USERNAME));


        String profileImageUrl = mCursor.getString(mCursor.getColumnIndex(ProfileEntry.PROFILE_IMAGE));
        if (profileImageUrl != null && !profileImageUrl.equals("")) {
            Uri profileImageUri = Uri.parse(profileImageUrl);
            profileItemHolder.profileImageView.setImageURI(profileImageUri);
        }

        String profileTitle = mCursor.getString(mCursor.getColumnIndex(ProfileEntry.PROFILE_NAME_TITLE));
        String profileFirstName = mCursor.getString(mCursor.getColumnIndex(ProfileEntry.PROFILE_FIRST_NAME));
        String profileLastName = mCursor.getString(mCursor.getColumnIndex(ProfileEntry.PROFIE_LAST_NAME));
        String displayName = profileTitle + " " + profileFirstName + " " + profileLastName;
        profileItemHolder.profileName.setText(displayName);

        int profileAge = mCursor.getInt(mCursor.getColumnIndex(ProfileEntry.PROFILE_AGE));
        String displayAge = Integer.toString(profileAge) + " yrs";
        profileItemHolder.profileAge.setText(displayAge);

        String profileGender = mCursor.getString(mCursor.getColumnIndex(ProfileEntry.PROFILE_GENDER));
        profileItemHolder.profileGender.setText(profileGender);


        String profileCity = mCursor.getString(mCursor.getColumnIndex(ProfileEntry.PROFILE_CITY));
        String profileState = mCursor.getString(mCursor.getColumnIndex(ProfileEntry.PROFILE_STATE));
        String displayLocation = profileCity + ", " + profileState;
        profileItemHolder.profileCity.setText(displayLocation);

        int acceptanceId = mCursor.getInt(mCursor.getColumnIndex(ProfileEntry.PROFILE_ACCEPTANCE_ID));
        if (acceptanceId == -1){
            profileItemHolder.declineButton.setVisibility(View.VISIBLE);
            profileItemHolder.acceptButton.setVisibility(View.VISIBLE);
            profileItemHolder.acceptanceResult.setVisibility(View.GONE);
        }
        else {
            profileItemHolder.declineButton.setVisibility(View.GONE);
            profileItemHolder.acceptButton.setVisibility(View.GONE);
            profileItemHolder.acceptanceResult.setVisibility(View.VISIBLE);

            if (acceptanceId == 0){
                profileItemHolder.acceptanceResult.setTextColor(ContextCompat.getColor(mContext, R.color.decline));
                profileItemHolder.acceptanceResult.setText(R.string.Declined);
            } else {
                profileItemHolder.acceptanceResult.setTextColor(ContextCompat.getColor(mContext, R.color.accept));
                profileItemHolder.acceptanceResult.setText(R.string.Accepted);
            }
        }

        profileItemHolder.declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mCursor.moveToPosition(position);

                String profileUsername = mCursor.getString(mCursor.getColumnIndex(ProfileEntry.PROFILE_USERNAME));

                int acceptanceId = 0;
                mClickHandler.onClick(profileUsername, acceptanceId, position);

            }
        });

        profileItemHolder.acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mCursor.moveToPosition(position);

                String profileUsername = mCursor.getString(mCursor.getColumnIndex(ProfileEntry.PROFILE_USERNAME));

                int acceptanceId = 1;
                mClickHandler.onClick(profileUsername, acceptanceId, position);

            }
        });

    }


}
