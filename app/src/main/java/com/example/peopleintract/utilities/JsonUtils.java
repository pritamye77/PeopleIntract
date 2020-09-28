package com.example.peopleintract.utilities;

import android.content.ContentValues;
import android.content.Context;

import com.example.peopleintract.db.ProfileContract.ProfileEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


// Utility function to handle Jason data.
public final class JsonUtils {

    private static final String PROFILE_RESULT = "results";

    private static final String PROFILE_LOGIN = "login";
    private static final String USERNAME = "username";

    private static final String PROFILE_GENDER = "gender";

    private static final String PROFILE_NAME = "name";
    private static final String TITLE = "title";
    private static final String FIRST_NAME = "first";
    private static final String LAST_NAME = "last";

    private static final String PROFILE_DOB = "dob";
    private static final String AGE = "age";

    private static final String PROFILE_EMAIL = "email";

    private static final String PROFILE_PICTURE = "picture";
    private static final String IMAGE_URL = "large";

    private static final String PROFILE_LOCATION = "location";
    private static final String CITY = "city";
    private static final String STATE = "state";


    public static ContentValues[] getProfileContentValuesFromJson(Context context, JSONObject profileJson)
            throws JSONException{


        JSONArray jsonProfileArrayList = profileJson.getJSONArray(PROFILE_RESULT);

        ContentValues[] profileContentValues = new ContentValues[jsonProfileArrayList.length()];

        for (int i = 0; i < jsonProfileArrayList.length(); i++) {

            String gender;
            String username;
            String title;
            String firstName;
            String lastName;
            String city;
            String email;
            String state;
            String imageUrl;
            int age;

            // Get JSON Object representing the single activity.
            JSONObject singleProfile = jsonProfileArrayList.getJSONObject(i);

            gender = singleProfile.getString(PROFILE_GENDER);

            JSONObject name = singleProfile.getJSONObject(PROFILE_NAME);
            title = name.getString(TITLE);
            firstName = name.getString(FIRST_NAME);
            lastName = name.getString(LAST_NAME);




            JSONObject location = singleProfile.getJSONObject(PROFILE_LOCATION);
            city = location.getString(CITY);
            state = location.getString(STATE);


            JSONObject login = singleProfile.getJSONObject(PROFILE_LOGIN);
            username = login.getString(USERNAME);

            JSONObject dob = singleProfile.getJSONObject(PROFILE_DOB);
            age = dob.getInt(AGE);

            JSONObject picture = singleProfile.getJSONObject(PROFILE_PICTURE);
            imageUrl = picture.getString(IMAGE_URL);



            ContentValues profileValues = new ContentValues();
            profileValues.put(ProfileEntry.PROFILE_GENDER, gender);
            profileValues.put(ProfileEntry.PROFILE_NAME_TITLE,title);
            profileValues.put(ProfileEntry.PROFILE_FIRST_NAME,firstName);
            profileValues.put(ProfileEntry.PROFIE_LAST_NAME, lastName);
            profileValues.put(ProfileEntry.PROFILE_CITY, city);
            profileValues.put(ProfileEntry.PROFILE_STATE, state);
            profileValues.put(ProfileEntry.PROFILE_USERNAME, username);
            profileValues.put(ProfileEntry.PROFILE_AGE, age);
            profileValues.put(ProfileEntry.PROFILE_IMAGE, imageUrl);
            profileValues.put(ProfileEntry.PROFILE_ACCEPTANCE_ID,-1);


            profileContentValues[i] = profileValues;
        }

        return profileContentValues;
    }


}
