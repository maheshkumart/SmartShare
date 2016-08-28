package com.rgu.share;

import android.net.Uri;

/**
 * Created by Raghu on 30/06/16.
 */
public class ShareItem {
    public Uri uri;
    public boolean isSelected;

    public ShareItem(Uri uri) {
        this.uri = uri;
    }
}
