package com.nutrition.express.main;

import com.nutrition.express.common.BasePresenter;
import com.nutrition.express.common.BaseView;
import com.nutrition.express.model.data.bean.PhotoPostsItem;

import java.util.List;

/**
 * Created by huang on 11/2/16.
 */

public interface DashboardContract {
    interface Presenter extends BasePresenter<View> {
        void getDashboard();
        void getNextDashboard();
        void refresh();
    }

    interface View extends BaseView {
        void showDashboard(List<PhotoPostsItem> blogPosts, boolean hasNext);
        void resetData(List<PhotoPostsItem> blogPosts, boolean hasNext);
    }

}
