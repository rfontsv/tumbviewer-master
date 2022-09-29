package com.nutrition.express.model.rest;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nutrition.express.application.Constants;
import com.nutrition.express.model.rest.ApiService.BlogService;
import com.nutrition.express.model.rest.ApiService.ReblogService;
import com.nutrition.express.model.rest.ApiService.TaggedService;
import com.nutrition.express.model.rest.ApiService.UserService;
import com.nutrition.express.model.rest.intercept.OAuth1SigningInterceptor;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by huang on 2/17/16.
 */
public class RestClient {
    private Retrofit retrofit;
    private OkHttpClient okHttpClient;

    private static class Holder {
        private static RestClient holder = new RestClient();
    }

    public static RestClient getInstance() {
        return Holder.holder;
    }

    private RestClient() {
    }

    public void init(Context context) {
        Cache cache = new Cache(context.getCacheDir(), 10 * 1024 * 1024);
        HttpLoggingInterceptor.Logger logger = new  HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                if (message.startsWith("{") || message.startsWith("[")) {
                    Logger.json(message);
                } else {
                    Log.d("okhttp", "" + message);
                }
            }
        };
        Logger.addLogAdapter(new AndroidLogAdapter());
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(logger);
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new OAuth1SigningInterceptor())
                .addInterceptor(loggingInterceptor)
                .cache(cache)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient)
                .build();
    }

    public void cancelAllCall() {
        okHttpClient.dispatcher().cancelAll();
    }

    public OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }

    public BlogService getBlogService() {
        return retrofit.create(BlogService.class);
    }

    public UserService getUserService() {
        return retrofit.create(UserService.class);
    }

    public TaggedService getTaggedService() {
        return retrofit.create(TaggedService.class);
    }

    public ReblogService getReblogService() {
        return retrofit.create(ReblogService.class);
    }

    public <T> T createService(Class<T> tClass) {
        return retrofit.create(tClass);
    }

}
