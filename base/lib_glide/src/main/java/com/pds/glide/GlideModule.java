package com.pds.glide;

import android.content.Context;

import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.module.AppGlideModule;

/**
 * @author pengdaosong
 */
@com.bumptech.glide.annotation.GlideModule
public class GlideModule extends AppGlideModule {
    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        super.applyOptions(context, builder);
    }
}
