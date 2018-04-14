package ask.op.sdk.common;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.DisplayMetrics;
import android.util.Pair;

import java.io.File;
import java.util.List;

public class PkgUtils {

    private static final boolean DEBUG = BuildConfig.DEBUG;
    private static final String TAG = "PkgUtils";

    // 厂商定制兼容问题
    @Deprecated
    @SuppressWarnings("unchecked")
    public static Pair<ActivityInfo, IntentFilter[]> queryMainLaunchActivityInfo(Context cxt, File apkFile) {
        try {
            Class<?> clazz = Class.forName("android.content.pm.PackageParser");
            Object packageParser = ReflectUtils.newInstance(clazz, new Class[]{String.class}, new Object[]{apkFile.getAbsolutePath()});
            DisplayMetrics dm = new DisplayMetrics();
            Object packageObj = ReflectUtils.getMethod(clazz, "parsePackage",
                    File.class, String.class, DisplayMetrics.class, int.class)
                    .invoke(packageParser, apkFile, apkFile.getAbsolutePath(), dm, 0);
            List activities = (List) ReflectUtils.getField(packageObj.getClass(), "activities").get(packageObj);
            for (Object data : activities) {
                clazz = data.getClass();
                List<IntentFilter> filters = (List) ReflectUtils.getField(clazz, "intents").get(data);
                for (IntentFilter filter : filters) {
                    boolean find = false;
                    for (int i = 0; i < filter.countActions(); i++) {
                        if (Intent.ACTION_MAIN.equals(filter.getAction(i))) {
                            find = true;
                            break;
                        }
                    }

                    if (!find) {
                        continue;
                    }
                    for (int i = 0; i < filter.countCategories(); i++) {
                        if (Intent.CATEGORY_LAUNCHER.equals(filter.getCategory(i))) {
                            return new Pair<>((ActivityInfo) ReflectUtils.getField(clazz, "info").get(data),
                                    filters.toArray(new IntentFilter[filters.size()]));
                        }
                    }
                }
            }
        } catch (Exception e) {
            if (DEBUG) {
                L.e(TAG, e);
            }
        }
        return null;
    }

    public static PackageInfo getPackageInfo(Context cxt, String apkPath) {
        return cxt.getPackageManager().getPackageArchiveInfo(apkPath,
                PackageManager.GET_ACTIVITIES);
    }

//    static ResolveInfo queryMainActivity(Context cxt) {
//        Intent intent = new Intent(Intent.ACTION_MAIN);
//        intent.addCategory(Intent.CATEGORY_LAUNCHER);
//        List<ResolveInfo> rs = cxt.getPackageManager().queryIntentActivities(intent,
//                PackageManager.MATCH_DEFAULT_ONLY);
//        if (null != rs && rs.size() > 0) {
//            return rs.get(0);
//        }
//        return null;
//    }
}
