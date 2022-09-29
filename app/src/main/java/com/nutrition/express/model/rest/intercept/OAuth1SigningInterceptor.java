/*
 * Copyright (C) 2015 Jake Wharton
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nutrition.express.model.rest.intercept;


import android.text.TextUtils;

import com.nutrition.express.model.data.DataManager;
import com.nutrition.express.model.data.bean.TumblrAccount;
import com.nutrition.express.model.helper.OAuth1SigningHelper;

import java.io.IOException;
import java.util.SortedMap;
import java.util.TreeMap;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

public final class OAuth1SigningInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        String auth = request.header("Authorization");
        if (TextUtils.equals(request.url().host(), "api.tumblr.com") &&
                DataManager.getInstance().isLogin() && TextUtils.isEmpty(auth)) {
            request = signRequest(request);
        }
        return chain.proceed(request);
    }

    public Request signRequest(Request request) throws IOException {
        SortedMap<String, String> parameters = new TreeMap<>();
        HttpUrl url = request.url();
        for (int i = 0; i < url.querySize(); i++) {
            parameters.put(url.queryParameterName(i), url.queryParameterValue(i));
        }
        String baseUrl = url.newBuilder().query(null).build().toString();

        Buffer body = new Buffer();

        RequestBody requestBody = request.body();
        if (requestBody != null) {
            requestBody.writeTo(body);
        }

        while (!body.exhausted()) {
            long keyEnd = body.indexOf((byte) '=');
            if (keyEnd == -1)
                throw new IllegalStateException("Key with no value: " + body.readUtf8());
            String key = body.readUtf8(keyEnd);
            body.skip(1); // Equals.

            long valueEnd = body.indexOf((byte) '&');
            String value = valueEnd == -1 ? body.readUtf8() : body.readUtf8(valueEnd);
            if (valueEnd != -1) body.skip(1); // Ampersand.

            parameters.put(key, value);
        }

        TumblrAccount account = DataManager.getInstance().getPositiveAccount();
        if (account == null) {
            return request;
        }
        String auth = new OAuth1SigningHelper(account.getApiKey(), account.getApiSecret())
                .buildAuthHeader(request.method(), baseUrl,
                        account.getToken(), account.getSecret(), parameters);

        return request.newBuilder().header("Authorization", auth).build();
    }


}
