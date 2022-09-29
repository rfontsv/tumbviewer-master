package com.nutrition.express.common;

import android.view.View;
import android.widget.TextView;

import com.nutrition.express.R;

/**
 * Created by huang on 11/9/16.
 */

public class ErrorViewHolder extends CommonViewHolder<Object> {
    public ErrorViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void bindView(Object object) {
        if (itemView instanceof TextView && object instanceof String) {
            object = object == null ? itemView.getContext().getString(R.string.load_failed) : object;
            ((TextView) itemView).setText((String) object);
        }
    }
}
