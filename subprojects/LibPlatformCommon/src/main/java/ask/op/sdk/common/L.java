package ask.op.sdk.common;

import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;

public class L {

    private static final String TAG = "tf:sdk";

    public static void v(String subTag, String msg) {
        Log.v(TAG, getLogMsg(subTag, msg));
    }

    public static void d(String subTag, String msg) {
        Log.d(TAG, getLogMsg(subTag, msg));
    }

    public static void i(String subTag, String msg) {
        Log.i(TAG, getLogMsg(subTag, msg));
    }

    public static void w(String subTag, String msg) {
        Log.w(TAG, getLogMsg(subTag, msg));
    }

    public static void w(String subTag, String msg, Throwable e) {
        Log.w(TAG, getLogMsg(subTag, msg + " Exception: " + getExceptionMsg(e)));
    }

    public static void e(String subTag, String msg) {
        Log.e(TAG, getLogMsg(subTag, msg));
    }

    public static void e(String subTag, Throwable e) {
        Log.e(TAG, getLogMsg(subTag, getExceptionMsg(e)));
    }

    public static void e(String subTag, String msg, Throwable e) {
        Log.e(TAG, getLogMsg(subTag, msg + " Exception: " + getExceptionMsg(e)));
    }

    private static String getLogMsg(String subTag, String msg) {
        return "[" + subTag + "] " + msg;
    }

    private static String getExceptionMsg(Throwable e) {
        StringWriter sw = new StringWriter(1024);
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        pw.close();
        return sw.toString();
    }
}
