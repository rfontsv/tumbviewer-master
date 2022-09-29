package com.nutrition.express.following;

import com.nutrition.express.common.BasePresenter;
import com.nutrition.express.common.BaseView;
import com.nutrition.express.model.rest.bean.FollowingBlog;

import java.util.List;

/**
 * Created by huang on 10/18/16.
 */

public interface FollowingContract {
    interface FollowersPresenter extends BasePresenter<View> {
        void getMyFollowing();
        void getNextFollowing();
    }

    interface View extends BaseView {
        void showFollowing(List<FollowingBlog.Blog> blogs, boolean hasNext);
    }
}
