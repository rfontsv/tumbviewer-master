package com.nutrition.express.login;

import com.nutrition.express.common.BasePresenter;
import com.nutrition.express.common.BaseView;

/**
 * Created by huang on 10/17/16.
 */

public interface LoginContract {
    interface LoginPresenter extends BasePresenter<View> {
        void getRequestToken();
        void getAccessToken(String oauthVerifier);
    }

    interface View extends BaseView {
        void loadUrl(String url);
        void showLoginSuccess();
    }
}
