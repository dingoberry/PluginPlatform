package ask.op.sdk.host;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.view.ContextThemeWrapper;

import ask.op.sdk.common.L;

class PluginContext extends ContextThemeWrapper {

    private static final boolean DEBUG = BuildConfig.DEBUG;
    private static final String TAG = "PluginContext";

    private PluginInfo mPluginInfo;

    PluginContext(PluginInfo pluginInfo, Context cxt) {
        super(cxt, R.style.Theme_AppCompat);
        mPluginInfo = pluginInfo;
    }

    @Override
    public String getPackageResourcePath() {
        return mPluginInfo.apkPath;
    }

    @Override
    public String getPackageCodePath() {
        return mPluginInfo.apkPath;
    }

    @Override
    public String getPackageName() {
        return mPluginInfo.packageInfo.packageName;
    }

    @Override
    public ClassLoader getClassLoader() {
        return mPluginInfo.classLoader;
    }

    @Override
    public AssetManager getAssets() {
        return mPluginInfo.assetManager;
    }

    @Override
    public Resources getResources() {
        return mPluginInfo.resources;
    }

    @Override
    public ApplicationInfo getApplicationInfo() {
        return mPluginInfo.packageInfo.applicationInfo;
    }

    @Override
    public ComponentName startService(Intent service) {
        if (DEBUG) {
            L.i(TAG, "PluginContext:startService:" + service.getComponent());
        }

        return super.startService(service);
    }
}
