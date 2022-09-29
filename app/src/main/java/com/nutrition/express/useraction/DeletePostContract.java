package com.nutrition.express.useraction;

import com.nutrition.express.common.BasePresenter;
import com.nutrition.express.common.BaseView;

/**
 * Created by huang on 12/12/16.
 */

public interface DeletePostContract {
    interface Presenter extends BasePresenter<View> {
        void deletePost(String blogName, String postId, int position);
    }

    interface View extends BaseView {
        void onDeletePostSuccess(int position);
    }
}
