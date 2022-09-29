package com.nutrition.express.likes;

import com.nutrition.express.common.BasePresenter;
import com.nutrition.express.common.BaseView;
import com.nutrition.express.model.data.bean.PhotoPostsItem;

import java.util.List;

/**
 * Created by huang on 11/8/16.
 */

public interface LikesContract {
    interface LikesPresenter extends BasePresenter<View> {
        void getLikePosts();
        void nextLikePosts();
        void getLikePosts(String name);
    }

    interface View extends BaseView {
        void showLikePosts(List<PhotoPostsItem> posts, boolean hasNext);
    }
}
