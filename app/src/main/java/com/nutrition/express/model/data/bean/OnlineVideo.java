package com.nutrition.express.model.data.bean;

import android.net.Uri;

import com.nutrition.express.application.ExpressApplication;
import com.nutrition.express.common.BaseVideoBean;
import com.nutrition.express.model.rest.bean.PostsItem;

/**
 * Created by huang on 2/23/17.
 */

public class OnlineVideo extends BaseVideoBean {

    public OnlineVideo(PostsItem postsItem) {
        String videoUrl = postsItem.getVideo_url();
        String thumbnailUrl = postsItem.getThumbnail_url();
        sourceUri = videoUrl == null ? Uri.EMPTY : Uri.parse(videoUrl);
        thumbnailUri = thumbnailUrl == null ? Uri.EMPTY : Uri.parse(thumbnailUrl);
        int videoWidth = postsItem.getThumbnail_width();
        int videoHeight = postsItem.getThumbnail_height();
        width = ExpressApplication.width;
        if (videoWidth > 0) {
            height = width * videoHeight / videoWidth;
        } else {
            height = width / 2;
        }

    }
}
