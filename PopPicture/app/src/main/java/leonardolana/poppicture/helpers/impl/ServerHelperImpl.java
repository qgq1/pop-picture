package leonardolana.poppicture.helpers.impl;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;

import java.io.IOException;
import java.io.InputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import leonardolana.poppicture.R;
import leonardolana.poppicture.helpers.api.ServerHelper;
import leonardolana.poppicture.server.ServerRequest;

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
public class ServerHelperImpl implements ServerHelper {

    private static ServerHelper INSTANCE;

    public static ServerHelper getInstance(Context context) {
        if (context instanceof Activity)
            throw new UnsupportedOperationException("To avoid leaks, use only application context");

        if (INSTANCE == null)
            INSTANCE = new ServerHelperImpl(context);

        return INSTANCE;
    }

    private Context mContext;
    private RequestQueue mRequestQueue;

    private ServerHelperImpl(Context context) {
        mContext = context;
        mRequestQueue = Volley.newRequestQueue(context);
        CookieManager cookieManage = new CookieManager();
        CookieHandler.setDefault(cookieManage);
    }

    @Override
    public void execute(ServerRequest serverRequest) {
        mRequestQueue.add(serverRequest.getRequest());
    }
}
