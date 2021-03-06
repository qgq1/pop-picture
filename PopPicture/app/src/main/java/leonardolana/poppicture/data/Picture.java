package leonardolana.poppicture.data;

import org.json.JSONObject;

import java.util.Random;

/**
 * Created by Leonardo Lana
 * Github: https://github.com/leonardodlana
 * <p>
 * Copyright 2018 Leonardo Lana
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class Picture {

    private String mUserId;
    private long mPictureId;
    private String mFileName;
    private String mTitle;
    private String mDescription;
    private int mLikesCount;
    private boolean mLikedByMe;
    private double mLatitude;
    private double mLongitude;
    private float mDistanceInKM;

    public Picture() {
        // Mock constructor
    }

    public Picture(String userId, long pictureId, String filename, String title, String description,
                   int likesCount, boolean likedByMe, double latitude, double longitude) {
        mUserId = userId;
        mPictureId = pictureId;
        mFileName = filename;
        mTitle = title;
        mDescription = description;
        mLikesCount = likesCount;
        mLikedByMe = likedByMe;
        mLatitude = latitude;
        mLongitude = longitude;
    }

    public String getUserId() {
        return mUserId;
    }

    public long getId() {
        return mPictureId;
    }

    public String getFileName() {
        return mFileName;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getDescription() {
        return mDescription;
    }

    public int getLikesCount() {
        return mLikesCount;
    }

    public boolean isLiked() {
        return mLikedByMe;
    }

    public void setLiked(boolean liked) {
        mLikedByMe = liked;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public float getDistanceInKM() {
        return mDistanceInKM;
    }

    public void setDistanceInKM(float distanceInKM) {
        mDistanceInKM = distanceInKM;
    }

    public JSONObject toJSON() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("user_public_id", getUserId());
            jsonObject.put("picture_id", getId());
            jsonObject.put("filename", getFileName());
            jsonObject.put("title", getTitle());
            jsonObject.put("description", getDescription());
            jsonObject.put("likes_count", getLikesCount());
            jsonObject.put("liked", mLikedByMe ? 1 : 0);
            jsonObject.put("latitude", getLatitude());
            jsonObject.put("longitude", getLongitude());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Picture fromJSON(JSONObject jsonObject) {
        try {
            return new Picture(jsonObject.getString("user_public_id"),
                    jsonObject.getLong("picture_id"),
                    jsonObject.getString("filename"),
                    jsonObject.getString("title"),
                    jsonObject.getString("description"),
                    jsonObject.getInt("likes_count"),
                    jsonObject.optInt("liked", 0) == 1,
                    jsonObject.getDouble("latitude"),
                    jsonObject.getDouble("longitude"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String getPath(Picture picture) {
        return picture.getUserId() + "/" + picture.getFileName() + ".jpg";
    }

    public static String getThumbPath(Picture picture) {
        return picture.getUserId() + "/" + picture.getFileName() + "_thumb.jpg";
    }

}
