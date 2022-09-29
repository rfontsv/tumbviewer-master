package com.nutrition.express.model.rest.ApiService;

import com.nutrition.express.model.rest.bean.BaseBean;
import com.nutrition.express.model.rest.bean.PostsItem;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by huang on 11/21/16.
 */

public interface TaggedService {
    @GET("/v2/tagged")
    Call<BaseBean<List<PostsItem>>> getTaggedPosts(@Query("tag") String tag,
                                                   @Query("filter") String filter,
                                                   @Query("before") long timestamp,
                                                   @Query("limit") int limit);

}
