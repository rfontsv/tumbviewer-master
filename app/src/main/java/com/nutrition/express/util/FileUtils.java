package com.nutrition.express.util;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;

import com.nutrition.express.application.ExpressApplication;
import com.nutrition.express.model.data.DataManager;

import java.io.File;

/**
 * Created by huang on 11/6/16.
 */

public class FileUtils {
    private static File getTumblrRootDir() {
        File tumblrDir = new File(Environment.getExternalStorageDirectory(), "tumblr");
        if (tumblrDir.exists()) {
            return tumblrDir;
        } else {
            return new File(Environment.getExternalStorageDirectory(), "Tumblr");
        }
    }

    public static File getVideoDir() {
        String accountName = DataManager.getInstance().getPositiveAccount().getName();
        if (TextUtils.isEmpty(accountName)) {
            return getPublicVideoDir();
        } else {
            return new File(getTumblrRootDir(), "video/" + accountName);
        }
    }

    public static File getImageDir() {
        String accountName = DataManager.getInstance().getPositiveAccount().getName();
        if (TextUtils.isEmpty(accountName)) {
            return getPublicImageDir();
        } else {
            return new File(getTumblrRootDir(), "image/" + accountName);
        }
    }

    public static File getPublicVideoDir() {
        return new File(getTumblrRootDir(), "video");
    }

    public static File getPublicImageDir() {
        return new File(getTumblrRootDir(), "image");
    }

    public static boolean imageSaved(Uri uri) {
        String name = uri.getLastPathSegment();
        if (null == name) {
            name = uri.toString();
        }
        return new File(FileUtils.getImageDir(), name).isFile();
    }

    public static File createImageFile(Uri uri) {
        File dir = getImageDir();
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String name = uri.getLastPathSegment();
        if (null == name) {
            name = uri.toString();
        }
        return new File(FileUtils.getImageDir(), name);
    }

     public static boolean videoSaved(Uri uri) {
        String name = uri.getLastPathSegment();
        if (null == name) {
            name = uri.toString();
        }
        return new File(FileUtils.getVideoDir(), name).isFile();
    }

    public static File createVideoFile(Uri uri) {
        File dir = getVideoDir();
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String name = uri.getLastPathSegment();
        if (null == name) {
            name = uri.toString();
        }
        return new File(FileUtils.getVideoDir(), name);
    }

    public static File createVideoFile(String url) {
        File dir = getVideoDir();
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String name;
        int index = url.lastIndexOf("/");
        if (index > 0) {
            name = url.substring(url.lastIndexOf("/"));
        } else {
            name = url;
        }
        return new File(dir, name);
    }

    public static void deleteFile(File file) {
        if (file.isDirectory()) {
            deleteDirFile(file);
        } else {
            if (file.delete()) {
                scanMedia(file);
            }
        }
    }

    private static void deleteDirFile(File dir) {
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                deleteDirFile(file);
            } else {
                if (file.delete()) {
                    scanMedia(file);
                }
            }
        }
        dir.delete();
    }

    private static void scanMedia(File f) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        ExpressApplication.getApplication().sendBroadcast(mediaScanIntent);
    }

}
