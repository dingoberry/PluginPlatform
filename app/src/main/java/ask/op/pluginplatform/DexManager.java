package ask.op.pluginplatform;

import android.content.Context;

import java.io.File;
import java.util.HashMap;

import dalvik.system.DexClassLoader;

/**
 * Created by tf on 4/3/2018.
 */

class DexManager {

    private static HashMap<String, DexClassLoader> sClsLoader = new HashMap<>();

    static void register(Context cxt, File apkFile) {
        Logger.i(apkFile.getAbsolutePath());
        sClsLoader.put(apkFile.getName(), new DexClassLoader(apkFile.getAbsolutePath(),
                cxt.getDir("plugin_cache", Context.MODE_PRIVATE).getAbsolutePath(),
                null, cxt.getClassLoader()));
    }

    static DexClassLoader getClassLoader(String key) {
        return sClsLoader.get(key);
    }
}
