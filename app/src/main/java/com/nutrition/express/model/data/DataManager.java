package com.nutrition.express.model.data;

import android.os.Build;
import android.text.TextUtils;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nutrition.express.application.Constants;
import com.nutrition.express.application.ExpressApplication;
import com.nutrition.express.model.data.bean.TumblrAccount;
import com.nutrition.express.model.data.bean.TumblrApp;
import com.nutrition.express.model.helper.LocalPersistenceHelper;
import com.nutrition.express.model.rest.bean.UserInfoItem;
import com.nutrition.express.settings.SettingsActivity;
import com.nutrition.express.util.PreferencesUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by huang on 10/18/16.
 */

public class DataManager {
    private static final String TUMBLR_APP = "tumblr_app";
    private static final String TUMBLR_ACCOUNT = "tumblr_account";

    private List<TumblrAccount> tumblrAccountList;
    private TumblrAccount positiveAccount;

    private transient long dayLimit, dayRemaining, dayReset;
    private transient long hourLimit, hourRemaining, hourReset;
    private boolean isSimpleMode;

    private UserInfoItem users;

    private List<Object> referenceBlog = new ArrayList<>();
    private Set<Object> referenceBlogSet = new HashSet<>();
    private Set<Object> followingSet = new HashSet<>();

    private static class Holder {
        private static DataManager holder = new DataManager();
    }

    public static DataManager getInstance() {
        return Holder.holder;
    }

    private DataManager() {
        loadTumblrAccounts();
        checkAccounts();
        isSimpleMode = PreferencesUtils.getBoolean(SettingsActivity.POST_SIMPLE_MODE, false);
    }

    private void loadTumblrAccounts() {
        tumblrAccountList = LocalPersistenceHelper.getShortContent(TUMBLR_ACCOUNT,
                new TypeToken<ArrayList<TumblrAccount>>(){}.getType());
        if (tumblrAccountList == null) {
            tumblrAccountList = new ArrayList<>();
            //check for updating from version 0.9.3
            String token = PreferencesUtils.getString("access_token");
            String secret = PreferencesUtils.getString("access_secret");
            if (!TextUtils.isEmpty(token)) {
                List<TumblrApp> tumblrAppList = LocalPersistenceHelper.getShortContent(TUMBLR_APP,
                        new TypeToken<ArrayList<TumblrApp>>(){}.getType());
                if (tumblrAppList == null || tumblrAppList.size() == 0) {
                    addAccount(Constants.CONSUMER_KEY, Constants.CONSUMER_SECRET, token, secret);
                } else {
                    for (TumblrApp app : tumblrAppList) {
                        if (app.isUsing()) {
                            addAccount(app.getApiKey(), app.getApiSecret(), token, secret);
                        }
                    }
                }

                PreferencesUtils.putString("access_token", null);
                PreferencesUtils.putString("access_secret", null);
            }
        }
    }

    private void checkAccounts() {
        for (TumblrAccount account : tumblrAccountList) {
            if (account.isUsing()) {
                positiveAccount = account;
            }
        }
        if (positiveAccount == null && tumblrAccountList.size() > 0) {
            positiveAccount = tumblrAccountList.get(0);
            positiveAccount.setUsing(true);
            LocalPersistenceHelper.storeShortContent(TUMBLR_ACCOUNT, tumblrAccountList);
        }
    }

    public TumblrAccount getPositiveAccount() {
        return positiveAccount;
    }

    public boolean isLogin() {
        return positiveAccount != null;
    }

    public List<TumblrAccount> getTumblrAccounts() {
        return tumblrAccountList;
    }

    public TumblrAccount addAccount(String apiKey, String apiSecret, String token, String secret) {
        TumblrAccount account = new TumblrAccount(apiKey, apiSecret, token, secret);
        if (positiveAccount == null) {
            positiveAccount = account;
            positiveAccount.setUsing(true);
        }
        tumblrAccountList.add(account);
        LocalPersistenceHelper.storeShortContent(TUMBLR_ACCOUNT, tumblrAccountList);
        return account;
    }

    public void removeAccount(TumblrAccount account) {
        tumblrAccountList.remove(account);
        if (account == positiveAccount) {
            positiveAccount = null;
            checkAccounts();
        }
        LocalPersistenceHelper.storeShortContent(TUMBLR_ACCOUNT, tumblrAccountList);
    }

    public void switchToAccount(TumblrAccount account) {
        for (TumblrAccount tumblrAccount : tumblrAccountList) {
            if (TextUtils.equals(tumblrAccount.getToken(), account.getToken())) {
                if (positiveAccount != null) {
                    positiveAccount.setUsing(false);
                }
                positiveAccount = tumblrAccount;
                positiveAccount.setUsing(true);
                break;
            }
        }
        LocalPersistenceHelper.storeShortContent(TUMBLR_ACCOUNT, tumblrAccountList);
        clearReferenceBlog();
    }

    /**
     * search account that its name equals to the positive account's name;
     * @return
     */
    public boolean switchToNextRoute() {
        if (positiveAccount == null || TextUtils.isEmpty(positiveAccount.getName())) {
            return false;
        }
        List<TumblrAccount> list = new ArrayList<>();
        for (TumblrAccount account : tumblrAccountList) {
            if (positiveAccount.getName().equals(account.getName())) {
                list.add(account);
            }
        }
        positiveAccount.setLimitExceeded(true);
        if (list.size() > 1) {
            for (TumblrAccount account : list) {
                if (account != positiveAccount && !account.isLimitExceeded()) {
                    switchToAccount(account);
                    return true;
                }
            }
        }
        return false;
    }

    public int getAccountCount() {
        HashSet<String> set = new HashSet<>();
        for (TumblrAccount tumblrAccount : tumblrAccountList) {
            set.add(tumblrAccount.getName());
        }
        return set.size();
    }

    public void clearCookies() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
        } else {
            CookieSyncManager cookieSyncManager =
                    CookieSyncManager.createInstance(ExpressApplication.getApplication());
            cookieSyncManager.startSync();
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            cookieSyncManager.startSync();
            cookieSyncManager.sync();
        }
    }

    public TumblrApp getTumblrApp() {
        List<TumblrApp> tumblrAppList = LocalPersistenceHelper
                .getShortContent(TUMBLR_APP, new TypeToken<ArrayList<TumblrApp>>(){}.getType());
        if (tumblrAppList != null && tumblrAppList.size() > 0) {
            return tumblrAppList.get(0);
        }
        return null;
    }

    public void saveTumblrApp(String key, String secret) {
        List<TumblrApp> tumblrAppList = new ArrayList<>();
        tumblrAppList.add(new TumblrApp(key, secret));
        LocalPersistenceHelper.storeShortContent(TUMBLR_APP, tumblrAppList);
    }

    public HashMap<String, String> getDefaultTumplrApps() {
        return new Gson().fromJson(Constants.API_KEYS,
                new TypeToken<HashMap<String, String>>(){}.getType());
    }

    public void updateTumblrAppInfo(String dayLimit, String dayRemaining, String dayReset,
                                    String hourLimit, String hourRemaining, String hourReset) {
        try {
            this.dayLimit = Long.valueOf(dayLimit);
            this.dayRemaining = Long.valueOf(dayRemaining);
            this.dayReset = Long.valueOf(dayReset);
            this.hourLimit = Long.valueOf(hourLimit);
            this.hourRemaining = Long.valueOf(hourRemaining);
            this.hourReset = Long.valueOf(hourReset);
        } catch (NumberFormatException e) {
        }
    }

    public long getDayLimit() {
        return dayLimit;
    }

    public long getDayRemaining() {
        return dayRemaining;
    }

    public long getDayReset() {
        return dayReset;
    }

    public long getHourLimit() {
        return hourLimit;
    }

    public long getHourRemaining() {
        return hourRemaining;
    }

    public long getHourReset() {
        return hourReset;
    }

    public UserInfoItem getUsers() {
        return users;
    }

    public void setUsers(UserInfoItem users) {
        this.users = users;
        if (positiveAccount != null && TextUtils.isEmpty(positiveAccount.getName())) {
            positiveAccount.setName(users.getName());
            LocalPersistenceHelper.storeShortContent(TUMBLR_ACCOUNT, tumblrAccountList);
        }
    }

    public List<Object> getReferenceBlog() {
        return referenceBlog;
    }

    public void addReferenceBlog(String blog) {
        if (!followingSet.contains(blog)) {
            if (referenceBlogSet.add(blog)) {
                referenceBlog.add(blog);
            }
        }
    }

    public void addFollowingBlog(String blog) {
        followingSet.add(blog);
    }

    private void clearReferenceBlog() {
        referenceBlog.clear();
        referenceBlogSet.clear();
        followingSet.clear();
    }

    public boolean isSimpleMode() {
        return isSimpleMode;
    }

    public void setSimpleMode(boolean simpleMode) {
        isSimpleMode = simpleMode;
    }

    /**
     * helper for shared element transition
     */
    private int position;

    public void setPosition(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }
}
