package ask.op.sdk.host;


import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;

import java.util.HashMap;

import ask.op.sdk.common.L;
import ask.op.sdk.common.PkgUtils;
import dalvik.system.DexClassLoader;

public class PluginManager {

    private static final boolean DEBUG = BuildConfig.DEBUG;
    private static final String TAG = "PluginManager";

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
