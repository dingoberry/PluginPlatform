package ask.op.pluginplatform;

import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.HashMap;

import ask.op.pluginplatform.DexManager.DexConfig;

public class ProxyService extends Service {

    private DexConfig mConfig;
    private HashMap<String, Service> mTargetMap;

    private Service initTarget(Intent intent) {
        if (null == intent) {
            return null;
        }
        String serviceName = intent.getStringExtra(Constants.EXTRA_TARGET);
        if (null == serviceName) {
            return null;
        }
        Service target = mTargetMap.get(serviceName);
        if (null != target) {
            return target;
        }

        if (null == mConfig) {
            String key = intent.getStringExtra(Constants.EXTRA_KEY);
            mConfig = DexManager.getClassLoader(key);
        }

        try {
            Class<?> clz = mConfig.classLoader.loadClass(serviceName);
            target = (Service) ReflectUtils.newInstance(clz);
            mTargetMap.put(serviceName, target);
        } catch (Exception e) {
            Logger.e(e);
        }
        return target;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Service service = initTarget(intent);
        if (null != service) {
            return service.onBind(intent);
        }
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mTargetMap = new HashMap<>();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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
        String serviceName = intent.getStringExtra(Constants.EXTRA_TARGET);
        if (null != serviceName) {
            Service service = mTargetMap.remove(serviceName);
            if (null != service) {
                service.onDestroy();
            }
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
        String serviceName = intent.getStringExtra(Constants.EXTRA_TARGET);
        return null != serviceName && mTargetMap.get(serviceName).onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        Service service = initTarget(intent);
        if (null != service) {
            service.onRebind(intent);
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Service service = initTarget(intent);
        if (null != service) {
            return service.onStartCommand(intent, flags, startId);
        }
        return super.onStartCommand(intent, flags, startId);
    }
}
