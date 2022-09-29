package com.nutrition.express.useraction;

import com.nutrition.express.common.BasePresenter;

/**
 * Created by huang on 11/7/16.
 */

public interface LikePostContract {
    interface Presenter extends BasePresenter<View> {
        void like(long id, String reblogKey);
        void unlike(long id, String reblogKey);
    }

    interface View {
        void onLike(long id);
        void onLikeFailure();
        void onUnlike(long id);
        void onUnlikeFailure();
    }
}
