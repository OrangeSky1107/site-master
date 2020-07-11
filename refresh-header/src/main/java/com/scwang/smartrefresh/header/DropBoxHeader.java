package com.scwang.smartrefresh.header;

import android.content.Context;
import android.util.AttributeSet;

import com.scwang.smartrefresh.layout.api.RefreshHeader;

import androidx.annotation.Nullable;

/**
 * DropBoxRefresh
 * Created by scwang on 2017/6/24.
 * design https://dribbble.com/shots/3470499-DropBox-Refresh
 */
public class DropBoxHeader extends com.scwang.smart.refresh.header.DropBoxHeader implements RefreshHeader {

    //<editor-fold desc="View">
    public DropBoxHeader(Context context) {
        this(context, null);
    }

    public DropBoxHeader(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }
    //</editor-fold>
}
