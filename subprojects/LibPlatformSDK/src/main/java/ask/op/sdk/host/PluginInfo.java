package ask.op.sdk.host;

import android.content.pm.PackageInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;

class PluginInfo {
    String apkPath;
    ClassLoader classLoader;
    AssetManager assetManager;
    Resources resources;
    PackageInfo packageInfo;
}
