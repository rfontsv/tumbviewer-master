package com.nutrition.express.main;

import com.nutrition.express.common.BasePresenter;
import com.nutrition.express.common.BaseView;
import com.nutrition.express.model.rest.bean.UserInfo;

/**
 * Created by huang on 11/2/16.
 */

public interface UserContract {
    interface Presenter extends BasePresenter<View> {
        void getMyInfo();
    }

    interface View extends BaseView {
        void showMyInfo(UserInfo info);
    }

}
