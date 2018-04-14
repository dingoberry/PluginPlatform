package ask.op.sdk.host;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.HashMap;

import ask.op.sdk.common.L;

public class PluginHostService extends Service {

    private static final boolean DEBUG = BuildConfig.DEBUG;
    private static final String TAG = "PluginHostService";

    private HashMap<String, Service> mTargetMap;

    private Service initTarget(Intent intent) {
        if (null == intent) {
            if (DEBUG) {
                L.i(TAG, "initTarget:null Intent");
            }
            return null;
        }
        ComponentName cn = intent.getComponent();
        if (null == cn) {
            if (DEBUG) {
                L.i(TAG, "initTarget:null ComponentName");
            }
            return null;
        }
        String serviceName = cn.getClassName();
        Service target = mTargetMap.get(serviceName);
        if (null != target) {
            if (DEBUG) {
                L.i(TAG, "initTarget:get Service");
            }
            return target;
        }

        PluginInfo info = PluginManager.getInstance(this).queryPluginInfo(cn.getPackageName());
        if (null == info) {
            if (DEBUG) {
                L.i(TAG, "initTarget:null PluginInfo");
            }
            return null;
        }

        try {
            Class<?> clz = info.classLoader.loadClass(serviceName);
            target = (Service) clz.newInstance();
            mTargetMap.put(serviceName, target);
        } catch (Exception e) {
            if (DEBUG) {
                L.e(TAG, e);
            }
        }
        return target;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mTargetMap = new HashMap<>();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        for (Service service : mTargetMap.values()) {
            service.onConfigurationChanged(newConfig);
        }
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean stopService(Intent intent) {
        Intent originIntent = PluginManager.getIntent(intent);
        if (null == originIntent) {
            return false;
        }
        ComponentName cn = originIntent.getComponent();
        if (null == cn) {
            if (DEBUG) {
                L.i(TAG, "stopService:null ComponentName");
            }
            return false;
        }

        Service service = mTargetMap.remove(cn.getClassName());
        if (null != service) {
            service.onDestroy();
        }
        if (mTargetMap.isEmpty()) {
            stopSelf();
        }
        return true;
    }

    @Override
    public void onLowMemory() {
        for (Service service : mTargetMap.values()) {
            service.onLowMemory();
        }
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        for (Service service : mTargetMap.values()) {
            service.onLowMemory();
        }
        super.onTrimMemory(level);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Intent originIntent = PluginManager.getIntent(intent);
        if (null == originIntent) {
            if (DEBUG) {
                L.i(TAG, "onUnbind:null Intent");
            }
            return false;
        }
        ComponentName cn = originIntent.getComponent();
        return null != cn && mTargetMap.get(cn.getClassName()).onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        Intent originIntent = PluginManager.getIntent(intent);
        if (null == originIntent) {
            if (DEBUG) {
                L.i(TAG, "onRebind:null Intent");
            }
            return;
        }
        ComponentName cn = originIntent.getComponent();
        if (null == cn) {
            if (DEBUG) {
                L.i(TAG, "onRebind:null ComponentName");
            }
            return;
        }
        Service service = initTarget(originIntent);
        if (null != service) {
            if (DEBUG) {
                L.i(TAG, "onRebind:null Service");
            }
            service.onRebind(intent);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Intent originIntent = PluginManager.getIntent(intent);
        Service service = initTarget(originIntent);
        if (null != service) {
            if (DEBUG) {
                L.i(TAG, "onBind:null Service");
            }
            return service.onBind(originIntent);
        }
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (DEBUG) {
            L.i(TAG, "onStartCommand");
        }
        if (null != intent) {
            if (DEBUG) {
                L.i(TAG, "onStartCommand: " + intent);
            }
            Intent originIntent = PluginManager.getIntent(intent);
            Service service = initTarget(originIntent);
            if (null != service) {
                return service.onStartCommand(originIntent, flags, startId);
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }
}
