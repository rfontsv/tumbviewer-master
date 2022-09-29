package com.nutrition.express.application;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.StrictMode;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.nutrition.express.BuildConfig;
import com.nutrition.express.MyEventBusIndex;
import com.nutrition.express.model.rest.RestClient;
import com.squareup.leakcanary.LeakCanary;

import org.greenrobot.eventbus.EventBus;

import io.fabric.sdk.android.Fabric;

import static android.content.ContentValues.TAG;

/**
 * Created by huang on 4/25/16.
 */
public class ExpressApplication extends Application {
    public static int width;
    public static int height;
    private static ExpressApplication application;
    private ImagePipelineConfig imagePipelineConfig;

    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);
        Fabric.with(this, new Crashlytics());
        Fabric.with(this, new Answers());

        application = this;
        DiskCacheConfig cacheConfig = DiskCacheConfig.newBuilder(this)
                .setMaxCacheSize(300 * 1024 * 1024)
                .build();
        DiskCacheConfig smallCacheConfig = DiskCacheConfig.newBuilder(this)
                .setMaxCacheSize(10 * 1024 * 1024)
                .build();
        imagePipelineConfig = ImagePipelineConfig.newBuilder(this)
                .setMainDiskCacheConfig(cacheConfig)
                .setSmallImageDiskCacheConfig(smallCacheConfig)
                .setDownsampleEnabled(true)
//                .setResizeAndRotateEnabledForNetwork(true)
                .setBitmapsConfig(Bitmap.Config.RGB_565)
                .build();
        Fresco.initialize(this, imagePipelineConfig);

        //init width and height
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        width = dm.widthPixels;
        height = dm.heightPixels;

        //init retrofit
        RestClient.getInstance().init(this);

        //init EventBus
        EventBus.builder().addIndex(new MyEventBusIndex()).installDefaultEventBus();

        //information
        if (BuildConfig.DEBUG) {
            ActivityManager  am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            Log.d(TAG, "onCreate: " + am.getMemoryClass());
            Log.d(TAG, "onCreate: " + am.getLargeMemoryClass());
            Log.d(TAG, "onCreate: " + Runtime.getRuntime().maxMemory());

            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectCustomSlowCalls()
                    .detectNetwork()
//                    .detectResourceMismatches()
                    .penaltyLog()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
//                    .penaltyDeath()
                    .build());
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Fresco.getImagePipeline().clearMemoryCaches();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        Fresco.getImagePipeline().clearMemoryCaches();
    }

    public static ExpressApplication getApplication() {
        return application;
    }

    public ImagePipelineConfig getImagePipelineConfig() {
        return imagePipelineConfig;
    }
}
