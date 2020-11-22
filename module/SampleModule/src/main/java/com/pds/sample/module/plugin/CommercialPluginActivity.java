package com.pds.sample.module.plugin;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PermissionInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.RemoteException;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.pds.plugin.helper.Log;
import com.pds.plugin.pm.PluginManager;
import com.pds.router.module.ModuleGroupRouter;
import com.pds.sample.R;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@Route(path = ModuleGroupRouter.PLUGIN_COMMERCIAL_PLUGIN)
public class CommercialPluginActivity extends AppCompatActivity {

    private static final String TAG = "CommercialPluginActivity";

    private ViewPager mViewPager;
    private FragmentStatePagerAdapter mFragmentStatePagerAdapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return new InstalledFragment();
            } else {
                return new ApkFragment();
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) {
                return "已安装";
            } else {
                return "待安装";
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mFragmentStatePagerAdapter);
//        getPerms();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void getPerms() {
        final PackageManager pm = getPackageManager();
        final List<PackageInfo> pkgs = pm.getInstalledPackages(PackageManager.GET_PERMISSIONS);
        new Thread() {
            @Override
            public void run() {
                try {
                    StringBuilder sb = new StringBuilder();
                    Log.e(TAG, "===========包权限start===========");
                    Set<String> ps = new TreeSet<String>();
                    for (PackageInfo pkg : pkgs) {
                        if (pkg.permissions != null && pkg.permissions.length > 0) {
                            for (PermissionInfo permission : pkg.permissions) {
                                ps.add(permission.name);
                            }
                        }
                    }
                    for (String p : ps) {
                        PermissionInfo permission = pm.getPermissionInfo(p, 0);
                        PackageInfo pkg = pm.getPackageInfo(permission.packageName, 0);
                        String re = String.format("<uses-permission android:name=\"%s\"/>", permission.name);
                        String ms = String.format("%s,%s,%s,%s,%s,%s,%s,%s", permission.packageName, pkg.applicationInfo.loadLabel(pm), permission.name, permission.group, permission.protectionLevel, permission.loadLabel(pm), permission.loadDescription(pm), re);
                        sb.append(ms).append("\r\n");
                        Log.e(TAG, "packageName=%s, name=%s group=%s protectionLevel=%s", permission.packageName, permission.name, permission.group, permission.protectionLevel);
                    }

                    FileWriter w = null;
                    try {
                        w = new FileWriter(new File(Environment.getExternalStorageDirectory(), "per.txt"));
                        w.write(sb.toString());

                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (w != null) {
                            try {
                                w.close();
                            } catch (IOException e) {
                            }
                        }
                    }
                    Log.e(TAG, "===========包权限end===========");
                } catch (NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private Handler mHandler = new Handler();

    private void doInstall(final File apkPath) {
        try {
            final PackageInfo info = getPackageManager().getPackageArchiveInfo(apkPath.getAbsolutePath(), PackageManager.GET_ACTIVITIES);
            if (info == null) {
                mHandler.post(() -> Toast.makeText(this, "apk损坏\n" + apkPath.getAbsolutePath(), Toast.LENGTH_SHORT).show());
                return;
            }
            final int re = PluginManager.getInstance().installPackage(apkPath.getAbsolutePath(), 0);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
