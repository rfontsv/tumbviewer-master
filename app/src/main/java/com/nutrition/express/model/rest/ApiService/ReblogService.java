package com.nutrition.express.model.rest.ApiService;

import com.nutrition.express.model.rest.bean.BaseBean;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by huang on 12/9/16.
 */

public interface ReblogService {

    @FormUrlEncoded
    @POST("/v2/blog/{id}/post/reblog")
    Call<BaseBean<Void>> reblogPost(@Path("id") String id, @FieldMap Map<String, String> para);
}
