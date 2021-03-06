package leonardolana.poppicture.server;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;

import leonardolana.poppicture.common.Execute;
import leonardolana.poppicture.helpers.api.RunnableExecutor;
import leonardolana.poppicture.helpers.api.ServerHelper;
import leonardolana.poppicture.helpers.api.UserHelper;

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

public abstract class ServerRequest implements Response.Listener<String>, Response.ErrorListener {

    public static final String KEY_ACTION = "action";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_FIREBASE_ID = "firebase_id";
    public static final String KEY_FILENAME = "filename";
    public static final String KEY_TITLE = "title";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_LATITUDE = "latitude";
    public static final String KEY_LONGITUDE = "longitude";
    public static final String KEY_USER_PUBLIC_ID = "user_public_id";
    public static final String KEY_LIKES_COUNT = "likes_count";
    public static final String KEY_PICTURE_ID = "picture_id";
    public static final String KEY_NAME = "name";
    public static final String KEY_PICTURE_NAME = "picture_name";
    public static final String KEY_LIKED = "liked";

    private JSONObject mParams = new JSONObject();
    private final String mURL;
    private RunnableExecutor mRunnableExecutor;
    private UserHelper mUserHelper;
    private ServerHelper mServerHelper;
    private RequestResponse mCallback;

    public ServerRequest(String url, String action) {
        addParam(KEY_ACTION, action);
        mURL = url;
    }

    public void addParam(String key, Object value) {
        try {
            mParams.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected void execute(@NonNull RunnableExecutor runnableExecutor, @NonNull ServerHelper serverHelper, @NonNull UserHelper userHelper, @NonNull RequestResponse callback) {
        mRunnableExecutor = runnableExecutor;
        mServerHelper = serverHelper;
        mUserHelper = userHelper;
        mCallback = callback;
        sendRequest();
    }

    private void sendRequest() {
        // Some requests return extra information if we have a session
        // When any request is going to be done and the user is logged in
        // we force an authentication before sending the original request.
        // By doing so, we are getting extra information
        if(!(this instanceof ServerRequestAuthorize) && mUserHelper.isUserLoggedIn()) {
            CookieManager cookieManager = (CookieManager) CookieManager.getDefault();
            List<HttpCookie> httpCookies = cookieManager.getCookieStore().get(URI.create(mURL));
            if(httpCookies == null || httpCookies.size() == 0) {
                new ServerRequestAuthorize(mUserHelper.getFirebaseId()).execute(mRunnableExecutor, mServerHelper, mUserHelper, new RequestResponse() {
                    @Override
                    public void onRequestSuccess(String data) {
                        mServerHelper.execute(ServerRequest.this);
                    }

                    @Override
                    public void onRequestError(RequestError error) {
                        mServerHelper.execute(ServerRequest.this);
                    }
                });

                return;
            }
        }

        mServerHelper.execute(this);
    }

    public final VolleyRequest getRequest() {
        return new ServerRequest.VolleyRequest(mURL, mParams, getTimeout(), shouldUseCacheIfAvailable(), ServerRequest.this, ServerRequest.this);
    }

    @Override
    public void onResponse(final String response) {
        // Check if onRequestSuccess from callback is annotated to run
        // in background
        Execute execute = null;
        try {
            execute = mCallback.getClass().getMethod("onRequestSuccess", String.class).getAnnotation(Execute.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        if(execute != null && execute.executeInBackground()) {
            mRunnableExecutor.executeInBackground(new Runnable() {
                @Override
                public void run() {
                    mCallback.onRequestSuccess(response);
                }
            });

            return;
        }

        mCallback.onRequestSuccess(response);
    }

    @Override
    public final void onErrorResponse(VolleyError error) {
        /*
            If the user tries to make a request that requires authorization
            and the user is unauthorized, we automatically send an authorization
            request and then send the original request again.
         */
        if(error instanceof AuthFailureError) {
            String firebaseId = mParams.optString(KEY_FIREBASE_ID);
            if (TextUtils.isEmpty(firebaseId))
                firebaseId = mUserHelper.getFirebaseId();

            new ServerRequestAuthorize(firebaseId).execute(mRunnableExecutor, mServerHelper, mUserHelper, new RequestResponse() {
                @Override
                public void onRequestSuccess(String data) {
                    sendRequest();
                }

                @Override
                public void onRequestError(RequestError error) {
                    mCallback.onRequestError(error);
                }
            });
            return;
        }

        mCallback.onRequestError(null);
    }

    protected int getTimeout() {
        return 5000;
    }

    protected boolean shouldUseCacheIfAvailable() {
        return false;
    }

    private class VolleyRequest extends StringRequest {

        private final JSONObject mJSON;

        public VolleyRequest(String url, JSONObject json, int timeout, boolean shouldUseCache, Response.Listener<String> listener, @Nullable Response.ErrorListener errorListener) {
            super(Method.POST, url, listener, errorListener);
            setShouldCache(shouldUseCache);
            setRetryPolicy(new DefaultRetryPolicy(timeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            mJSON = json;
            setShouldRetryServerErrors(false);
        }

        @Override
        public byte[] getBody() throws AuthFailureError {
            return mJSON.toString().getBytes(StandardCharsets.UTF_8);
        }

        @Override
        public String getBodyContentType() {
            return "application/json; charset=utf-8";
        }

    }

}
