package ask.op.sdk.host;


import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.AssetManager;
import android.content.res.Resources;

import java.util.HashMap;

import ask.op.sdk.common.L;
import ask.op.sdk.common.PkgUtils;
import dalvik.system.DexClassLoader;

public class PluginManager {

    private static final boolean DEBUG = BuildConfig.DEBUG;
    private static final String TAG = "PluginManager";

    private static final String ORIGIN_INTENT = "origin_intent";

    private static PluginManager sInstance;


    private Context mHostContext;
    private HashMap<String, PluginInfo> mPluginInfoMap;

    private PluginManager(Context context) {
        mHostContext = context.getApplicationContext();
        mPluginInfoMap = new HashMap<>();
    }

    public static PluginManager getInstance(Context context) {
        if (null == sInstance) {
            synchronized (PluginManager.class) {
                if (null == sInstance) {
                    sInstance = new PluginManager(context);
                }
            }
        }
        return sInstance;
    }

    static Intent getIntent(Activity cxt) {
        return getIntent(cxt.getIntent());
    }

    static Intent getIntent(Intent intent) {
        return intent.getParcelableExtra(ORIGIN_INTENT);
    }

    public static void startActivity(Context cxt, Intent intent) {
        Intent realIntent = new Intent(cxt, PluginHostActivity.class);
        realIntent.putExtra(ORIGIN_INTENT, intent);
        cxt.startActivity(realIntent);
    }

    public static void startActivityForResult(Activity activity, Intent intent, int requestCode) {
        Intent realIntent = new Intent(activity, PluginHostActivity.class);
        realIntent.putExtra(ORIGIN_INTENT, intent);
        activity.startActivityForResult(realIntent, requestCode);
    }

    public static ComponentName startService(Context cxt, Intent intent) {
        Intent realIntent = new Intent(cxt, PluginHostService.class);
        realIntent.putExtra(ORIGIN_INTENT, intent);
        return cxt.startService(realIntent);
    }

    public static boolean stopService(Context cxt, Intent name) {
        Intent realIntent = new Intent(cxt, PluginHostService.class);
        realIntent.putExtra(ORIGIN_INTENT, name);
        return cxt.stopService(realIntent);
    }

    public static boolean bindService(Context cxt, Intent service, ServiceConnection conn, int flags) {
        Intent realIntent = new Intent(cxt, PluginHostService.class);
        realIntent.putExtra(ORIGIN_INTENT, service);
        return cxt.bindService(realIntent, conn, flags);
    }

    PluginInfo queryPluginInfo(String pkgName) {
        return mPluginInfoMap.get(pkgName);
    }

    public String register(String pluginPath) {
        if (DEBUG) {
            L.i(TAG, "register plugin:" + pluginPath);
        }
        PluginInfo info = new PluginInfo();
        info.apkPath = pluginPath;
        info.classLoader = new DexClassLoader(pluginPath,
                mHostContext.getDir("plugin_cache", Context.MODE_PRIVATE).getAbsolutePath(),
                null, mHostContext.getClassLoader());
        try {
            info.classLoader.loadClass("ask.op.sub.subdemo1.MainActivity");
        } catch (ClassNotFoundException e) {
            if (DEBUG) {
                L.e(TAG, "register!", e);
            }
        }
        info.packageInfo = PkgUtils.getPackageInfo(mHostContext, pluginPath);

        try {
            info.assetManager = AssetManager.class.newInstance();
            AssetManager.class.getMethod("addAssetPath", String.class).invoke(info.assetManager, pluginPath);

            Resources r = mHostContext.getResources();
            info.resources = new Resources(info.assetManager, r.getDisplayMetrics(), r.getConfiguration());
        } catch (Exception e) {
            if (DEBUG) {
                L.e(TAG, "register error!", e);
            }
            return null;
        }
        mPluginInfoMap.put(info.packageInfo.packageName, info);
        return info.packageInfo.packageName;
    }
}
