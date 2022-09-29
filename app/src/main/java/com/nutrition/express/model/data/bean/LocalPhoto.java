package com.nutrition.express.model.data.bean;

import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.File;

/**
 * Created by huang on 2/22/17.
 */

public class LocalPhoto {
    private File file;
    private boolean checked;
    private Uri uri;
    private int width;
    private int height;

    public LocalPhoto(File file) {
        this.file = file;
        uri = Uri.fromFile(file);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getPath(), options);
        width = options.outWidth;
        height = options.outHeight;
    }

    public boolean isValid() {
        return width != -1 && height != -1;
    }

    public File getFile() {
        return file;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public Uri getUri() {
        return uri;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
