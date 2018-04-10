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

    private <T> T invokeMethod(Service target, String methodName, Object[] parameters, Class<?>[] parameterClzs) {
        if (null == target) {
            return null;
        }
        try {
            return (T) ReflectUtils.invokeMethod(target, methodName, parameters, parameterClzs);
        } catch (Exception e) {
            Logger.e(e);
        }
        return null;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return invokeMethod(initTarget(intent), "onBind", new Object[]{intent}, new Class[]{Intent.class});
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
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return invokeMethod(initTarget(intent), "onUnbind", new Object[]{intent}, new Class[]{Intent.class});
    }

    @Override
    public void onRebind(Intent intent) {
          invokeMethod(initTarget(intent), "onRebind", new Object[]{intent}, new Class[]{Intent.class});
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return (int) invokeMethod(initTarget(intent), "onStartCommand", new Object[]{intent, flags, startId},
                new Class[]{Intent.class, int.class, int.class});
    }
}
