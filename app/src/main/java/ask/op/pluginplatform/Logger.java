package ask.op.pluginplatform;

import android.util.Log;

class Logger {

    private static final String TAG = "yymm";

    static void i(String msg) {
        Log.i(TAG, msg);
    }

    static void e(String msg, Throwable e) {
        Log.i(TAG, msg, e);
    }

    static void e(Throwable e) {
        Log.i(TAG, "", e);
    }
}
