package com.example.peopleintract.db;

import android.net.Uri;
import android.provider.BaseColumns;

public class ProfileContract {

    public static final String CONTENT_AUTHORITY = "com.example.peopleintract.db.ProfileProvider";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_PROFILE = "profile";


    public static final class ProfileEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_PROFILE)
                .build();

        public static final String TABLE_NAME = "Profile_List";

        public static final String PROFILE_USERNAME ="Username";

        public static final String PROFILE_GENDER = "Gender";

        public static final String PROFILE_IMAGE = "Image";

        public static final String PROFILE_NAME_TITLE ="Name_Title";

        public static final String PROFILE_FIRST_NAME = "First_Name";

        public static final String PROFIE_LAST_NAME ="Last_Name";

        public static final String PROFILE_AGE = "Age";

        public static final String PROFILE_CITY ="City";

        public static final String PROFILE_STATE ="State";

        public static final String PROFILE_ACCEPTANCE_ID = "Acceptance_Value"; //  -1 id default
        // accepted(1) or declined(0)

        public static Uri buildProfileUriWithProfileUsername(String profileUsername) {
            return CONTENT_URI.buildUpon()
                    .appendPath(profileUsername)
                    .build();
        }

        public static String getSqlSelectForAllProfile(){
            return ProfileEntry.PROFILE_USERNAME;
        }

    }

}
