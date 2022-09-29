package com.nutrition.express.taggedposts;

import com.nutrition.express.common.BasePresenter;
import com.nutrition.express.common.BaseView;
import com.nutrition.express.model.data.bean.PhotoPostsItem;

import java.util.List;

/**
 * Created by huang on 11/21/16.
 */

public interface TaggedContract {
    interface Presenter extends BasePresenter<View> {
        void getTaggedPosts(String tag);
        void nextTaggedPosts(String tag);
    }

    interface View extends BaseView {
        void showTaggedPosts(List<PhotoPostsItem> postsItems, boolean hasNext);
    }
}
