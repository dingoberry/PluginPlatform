package ask.op.sdk.common;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;

public class PluginUtils {

    private static final String ORIGIN_INTENT = "origin_intent";
    private static final String PLATFORM_PACKAGE_NAME = "ask.op.pluginplatform";
    private static final String PROXY_HOST_ACTIVITY = "ask.op.sdk.host.PluginHostActivity";
    private static final String PROXY_HOST_SERVICE = "ask.op.sdk.host.PluginHostService";

    public static void startActivity(Context cxt, Intent intent) {
        Intent realIntent = new Intent();
        realIntent.setClassName(PLATFORM_PACKAGE_NAME, PROXY_HOST_ACTIVITY);
        realIntent.putExtra(ORIGIN_INTENT, intent);
        cxt.startActivity(realIntent);
    }

    public static void startActivityForResult(Activity activity, Intent intent, int requestCode) {
        Intent realIntent = new Intent();
        realIntent.setClassName(PLATFORM_PACKAGE_NAME, PROXY_HOST_ACTIVITY);
        realIntent.putExtra(ORIGIN_INTENT, intent);
        activity.startActivityForResult(realIntent, requestCode);
    }

    public static ComponentName startService(Context cxt, Intent intent) {
        Intent realIntent = new Intent();
        realIntent.setClassName(PLATFORM_PACKAGE_NAME, PROXY_HOST_SERVICE);
        realIntent.putExtra(ORIGIN_INTENT, intent);
        return cxt.startService(realIntent);
    }

    public static boolean stopService(Context cxt, Intent name) {
        Intent realIntent = new Intent();
        realIntent.setClassName(PLATFORM_PACKAGE_NAME, PROXY_HOST_SERVICE);
        realIntent.putExtra(ORIGIN_INTENT, name);
        return cxt.stopService(realIntent);
    }

    public static boolean bindService(Context cxt, Intent service, ServiceConnection conn, int flags) {
        Intent realIntent = new Intent();
        realIntent.setClassName(PLATFORM_PACKAGE_NAME, PROXY_HOST_SERVICE);
        realIntent.putExtra(ORIGIN_INTENT, service);
        return cxt.bindService(realIntent, conn, flags);
    }

    public static Intent getIntent(Activity cxt) {
        return getIntent(cxt.getIntent());
    }

    public static Intent getIntent(Intent intent) {
        return intent.getParcelableExtra(ORIGIN_INTENT);
    }
}
