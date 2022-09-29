package com.nutrition.express.useraction;

import com.nutrition.express.common.BasePresenter;

/**
 * Created by huang on 11/7/16.
 */

public interface FollowBlogContract {
    interface Presenter extends BasePresenter<View> {
        void follow(String url);
        void unfollow(String url);
    }

    interface View {
        void onFollow();
        void onUnfollow();
    }
}
