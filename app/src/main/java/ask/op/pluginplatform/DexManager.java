package ask.op.pluginplatform;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;

import java.io.File;
import java.util.HashMap;

import dalvik.system.DexClassLoader;

/**
 * Created by tf on 4/3/2018.
 */

class DexManager {

    private static HashMap<String, DexConfig> sClsLoader = new HashMap<>();

    static void register(Context cxt, File apkFile) {
        Logger.i(apkFile.getAbsolutePath());
        DexConfig config = new DexConfig();
        config.classLoader = new DexClassLoader(apkFile.getAbsolutePath(),
                cxt.getDir("plugin_cache", Context.MODE_PRIVATE).getAbsolutePath(),
                null, cxt.getClassLoader());
        config.packageInfo = PkgUtils.getPackageInfo(cxt, apkFile.getAbsolutePath());

        try {
            config.assetManager = AssetManager.class.newInstance();
            AssetManager.class.getMethod("addAssetPath", String.class).invoke(config.assetManager, apkFile.getAbsolutePath());

            Resources r = cxt.getResources();
            config.resources = new Resources(config.assetManager, r.getDisplayMetrics(), r.getConfiguration());
        } catch (Exception e) {
            Logger.e(e);
            return;
        }
        sClsLoader.put(apkFile.getName(), config);
    }

    static DexConfig getClassLoader(String key) {
        return sClsLoader.get(key);
    }

    private static Intent wrapIntent(Activity cxt, String key, Intent intent) {
        Intent realIntent = new Intent(cxt, ProxyActivity.class);
        realIntent.putExtra(Constants.EXTRA_TARGET, intent.getComponent().getClassName());
        realIntent.putExtra(Constants.EXTRA_KEY, key);
        return realIntent;
    }

    static ComponentName startService(Activity cxt, String key, Intent intent) {
        return cxt.startService(wrapIntent(cxt, key, intent));
    }

    static void bindService() {

    }

    static void startActivityForResult(Activity cxt, String key, Intent intent, int requestCode) {
        cxt.startActivityForResult(wrapIntent(cxt, key, intent), requestCode);
    }

    static class DexConfig {
        ClassLoader classLoader;
        AssetManager assetManager;
        Resources resources;
        PackageInfo packageInfo;
    }
}
