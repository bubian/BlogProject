package com.pds.tool;

import android.content.Context;

import com.facebook.stetho.Stetho;
import com.facebook.stetho.dumpapp.DumperContext;
import com.facebook.stetho.dumpapp.DumperPlugin;

/**
 * @author: pengdaosong
 * CreateTime:  2020-08-07 15:57
 * Email：pengdaosong@medlinker.com
 * Description:
 */
public class StethoTool {

    public void init(Context context) {
        Stetho.initialize(Stetho.newInitializerBuilder(context)
                .enableDumpapp(() -> new Stetho.DefaultDumperPluginsBuilder(context)
                        .provide(new CustomDumperPlugin())
                        .finish())
                .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(context))
                .build());
    }


    private class CustomDumperPlugin implements DumperPlugin {

        @Override
        public String getName() {
            return "CustomDumperPlugin";
        }

        @Override
        public void dump(DumperContext dumpContext) {

        }
    }
}
