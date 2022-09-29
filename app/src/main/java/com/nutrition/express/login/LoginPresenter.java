package com.nutrition.express.login;

import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.nutrition.express.application.Constants;
import com.nutrition.express.model.data.DataManager;
import com.nutrition.express.model.data.bean.TumblrAccount;
import com.nutrition.express.model.data.bean.TumblrApp;
import com.nutrition.express.model.helper.OAuth1SigningHelper;
import com.nutrition.express.model.rest.RestClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static android.content.ContentValues.TAG;

/**
 * Created by huang on 10/17/16.
 */

public class LoginPresenter implements LoginContract.LoginPresenter {
    private Handler handler = new Handler();
    private LoginContract.View view;
    private OkHttpClient okHttpClient;
    private boolean requesting = false;
    private String oauthToken, oauthTokenSecret;
    private int type;
    private TumblrApp tumblrApp;

    public LoginPresenter(LoginContract.View view, int type) {
        this.view = view;
        this.type = type;
        okHttpClient = RestClient.getInstance().getOkHttpClient();
        tumblrApp = getApiKey(type);
    }

    private TumblrApp getApiKey(int type) {
        if (type == LoginActivity.NEW_ACCOUNT || type == LoginActivity.NORMAL ||
                type == LoginActivity.ROUTE_SWITCH) {
            return selectUnusedTumblrApp(DataManager.getInstance().getDefaultTumplrApps());
        } else if (type == LoginActivity.NEW_ROUTE) {
            TumblrApp tumblrApp= DataManager.getInstance().getTumblrApp();
            if (tumblrApp == null) {
                return selectUnusedTumblrApp(DataManager.getInstance().getDefaultTumplrApps());
            } else {
                return tumblrApp;
            }
        }
        return new TumblrApp(Constants.CONSUMER_KEY, Constants.CONSUMER_SECRET);
    }

    /**
     * get tumblr app key for positive account, just route switching.
     *
     * @param map
     */
    private TumblrApp selectUnusedTumblrApp(HashMap<String, String> map) {
        List<TumblrAccount> accounts = DataManager.getInstance().getTumblrAccounts();
        if (accounts.size() < map.size()) {
            for (TumblrAccount account : accounts) {
                map.remove(account.getApiKey());
            }
        } else {
            TumblrAccount positiveAccount = DataManager.getInstance().getPositiveAccount();
            if (positiveAccount != null) {
                if (TextUtils.isEmpty(positiveAccount.getName())) {
                    map.remove(positiveAccount.getApiKey());
                } else {
                    for (TumblrAccount account : accounts) {
                        if (positiveAccount.getName().equals(account.getName())) {
                            map.remove(account.getApiKey());
                        }
                    }
                }
            }
        }

        if (map.size() > 0) {
            List<String> list = new ArrayList<>(map.keySet());
            int randomIndex = (int) (System.currentTimeMillis() / 1000) % list.size();
            String key = list.get(randomIndex);
            return new TumblrApp(key, map.get(key));
        } else {
            view.onError(-1, "Request limit exceeded, see you tomorrow");
            return null;
        }
    }

    @Override
    public void getRequestToken() {
        if (!requesting && tumblrApp != null) {
            requesting = true;
            String auth = new OAuth1SigningHelper(tumblrApp.getApiKey(), tumblrApp.getApiSecret())
                    .buildRequestHeader("POST", Constants.REQUEST_TOKEN_URL);
            Request request = new Request.Builder()
                    .url(Constants.REQUEST_TOKEN_URL)
                    .method("POST", RequestBody.create(MediaType.parse("text/plain; charset=utf-8"), ""))
                    .header("Authorization", auth)
                    .build();
            Call call = okHttpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    handler.post(new FailureRunnable(LoginPresenter.this, 0, e.getMessage()));
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    ResponseBody responseBody = response.body();
                    try {
                        if (response.isSuccessful()) {
                            String body = responseBody.string();
                            Log.d(TAG, "onResponse: " + body);
                            HashMap<String, String> hashMap = convert(body);
                            oauthToken = hashMap.get("oauth_token");
                            oauthTokenSecret = hashMap.get("oauth_token_secret");
                            handler.post(requestTokenSuccess);
                        } else {
                            handler.post(new FailureRunnable(LoginPresenter.this, response.code(),
                                    response.message()));
                        }
                    } finally {
                        responseBody.close();
                    }
                }
            });
        }
    }

    @Override
    public void getAccessToken(String oauthVerifier) {
        if (!requesting) {
            requesting = true;
            String auth = new OAuth1SigningHelper(tumblrApp.getApiKey(), tumblrApp.getApiSecret())
                    .buildAccessHeader("POST", Constants.ACCESS_TOKEN_URL,
                            oauthToken, oauthVerifier, oauthTokenSecret);
            Request request = new Request.Builder()
                    .url(Constants.ACCESS_TOKEN_URL)
                    .method("POST", RequestBody.create(MediaType.parse("text/plain; charset=utf-8"), ""))
                    .header("Authorization", auth)
                    .build();
            Call call = okHttpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    handler.post(accessTokenFailure);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    ResponseBody responseBody = response.body();
                    try {
                        if (response.isSuccessful()) {
                            String body = responseBody.string();
                            Log.d(TAG, "onResponse: " + body);
                            HashMap<String, String> hashMap = convert(body);
                            oauthToken = hashMap.get("oauth_token");
                            oauthTokenSecret = hashMap.get("oauth_token_secret");
                            handler.post(accessTokenSuccess);
                        } else {
                            handler.post(accessTokenFailure);
                        }
                    } finally {
                        responseBody.close();
                    }

                }
            });
        }
    }

    @Override
    public void onAttach(LoginContract.View view) {

    }

    @Override
    public void onDetach() {
        view = null;
    }

    //avoid holding the view in okhttp callback ?
    private static class FailureRunnable implements Runnable {
        private LoginPresenter presenter;
        private String msg;
        private int code;

        public FailureRunnable(LoginPresenter presenter, int code, String msg) {
            this.presenter = presenter;
            this.msg = msg;
            this.code = code;
        }

        @Override
        public void run() {
            if (presenter.view != null) {
                presenter.view.onError(code, msg);
            }
        }
    }

    //avoid holding the view in okhttp callback ?
    private Runnable requestTokenFailure = new Runnable() {
        @Override
        public void run() {
            if (view != null) {
                view.onError(0, "get request token failed");
            }
            requesting = false;
        }
    };

    private Runnable requestTokenSuccess = new Runnable() {
        @Override
        public void run() {
            if (view != null) {
                view.loadUrl(Constants.AUTHORIZE_URL + "?oauth_token=" + oauthToken);
            }
            requesting = false;
        }
    };

    private Runnable accessTokenFailure = new Runnable() {
        @Override
        public void run() {
            if (view != null) {
                view.onError(0, "get access token failed");
            }
            requesting = false;
        }
    };

    private Runnable accessTokenSuccess = new Runnable() {
        @Override
        public void run() {
            if (view != null) {
                view.showLoginSuccess();
                DataManager dataManager = DataManager.getInstance();
                TumblrAccount tumblrAccount = dataManager.addAccount(
                        tumblrApp.getApiKey(), tumblrApp.getApiSecret(), oauthToken, oauthTokenSecret);
                if (type == LoginActivity.NEW_ROUTE || type == LoginActivity.ROUTE_SWITCH) {
                    dataManager.switchToAccount(tumblrAccount);
                }
            }
            requesting = false;
        }
    };

    private HashMap<String, String> convert(String body) {
        HashMap<String, String> hashMap = new HashMap<>();
        String[] strings = body.split("&");
        for (String string : strings) {
            String[] pair = string.split("=");
            if (pair.length == 2) {
                hashMap.put(pair[0], pair[1]);
            }
        }
        return hashMap;
    }

}
