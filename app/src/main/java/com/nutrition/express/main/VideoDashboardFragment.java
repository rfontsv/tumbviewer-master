package com.nutrition.express.main;

import android.content.Context;
import android.view.View;

import com.nutrition.express.R;
import com.nutrition.express.blogposts.VideoPhotoPostVH;
import com.nutrition.express.common.CommonRVAdapter;
import com.nutrition.express.common.CommonViewHolder;
import com.nutrition.express.common.ExoPlayerInstance;
import com.nutrition.express.model.data.bean.VideoPostsItem;

/**
 * Created by huang on 11/2/16.
 */

public class VideoDashboardFragment extends DashboardFragment {
    @Override
    protected CommonRVAdapter getAdapter() {
        CommonRVAdapter.Builder builder = CommonRVAdapter.newBuilder();
        builder.addItemType(VideoPostsItem.class, R.layout.item_video_post,
                new CommonRVAdapter.CreateViewHolder() {
                    @Override
                    public CommonViewHolder createVH(View view) {
                        return new VideoPhotoPostVH(view, playerInstance);
                    }
                });
        builder.setLoadListener(this);
        return builder.build();
    }

    @Override
    protected String getType() {
        return "video";
    }

    private ExoPlayerInstance playerInstance;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (playerInstance == null) {
            playerInstance = ExoPlayerInstance.getInstance();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        playerInstance.resumePlayer();
    }

    @Override
    public void onStop() {
        super.onStop();
        playerInstance.releasePlayer();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isVisibleToUser && playerInstance != null) {
            playerInstance.pausePlayer();
        }
    }

}
