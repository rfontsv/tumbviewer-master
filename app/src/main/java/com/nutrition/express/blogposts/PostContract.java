package com.nutrition.express.blogposts;

import com.nutrition.express.common.BasePresenter;
import com.nutrition.express.common.BaseView;

/**
 * Created by huang on 7/13/16.
 */

public interface PostContract {
    interface Presenter extends BasePresenter<View> {
        void loadData(String blogName);
        void setShowType(int type);
        int getShowType();
    }

    interface View extends BaseView {
        void showData(Object[] items, boolean hasNext);
        void resetData(Object[] items, boolean hasNext);
        void onFollowed();
        void onUnfollowed();
        void hideFollowItem();
    }
}
