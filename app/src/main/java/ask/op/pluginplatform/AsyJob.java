package ask.op.pluginplatform;

import android.os.Handler;
import android.os.HandlerThread;

class AsyJob {

    private static Handler sSubHandler;
    private static Handler sUiHandler;

    static void init() {
        HandlerThread ht = new HandlerThread(AsyJob.class.getSimpleName());
        ht.start();

        sSubHandler = new Handler(ht.getLooper());
        sUiHandler = new Handler();
    }

    static void post(Runnable runnable) {
        if (null == sSubHandler) {
            throw new RuntimeException("AsyHandler is not initialized!");
        }
        sSubHandler.post(runnable);
    }

    static void post(Runnable runnable, long delay) {
        if (null == sSubHandler) {
            throw new RuntimeException("AsyHandler is not initialized!");
        }
        sSubHandler.postDelayed(runnable, delay);
    }

    static void runOnUI(Runnable runnable) {
        if (null == sUiHandler) {
            throw new RuntimeException("AsyHandler is not initialized!");
        }
        sUiHandler.post(runnable);
    }
}
