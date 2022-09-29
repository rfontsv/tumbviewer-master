package com.nutrition.express.reblog;

import com.nutrition.express.common.BasePresenter;
import com.nutrition.express.common.BaseView;

/**
 * Created by huang on 12/9/16.
 */

public interface ReblogContract {

    interface Presenter extends BasePresenter<View> {
        void reblog(String blogName, String blogId, String blogkey, String blogType, String comment);
    }

    interface View extends BaseView {
        void onSuccess();
    }

}
