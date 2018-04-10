package ask.op.pluginplatform;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.WindowManager;

import ask.op.pluginplatform.DexManager.DexConfig;

public class ProxyActivity extends Activity {

    private Activity mTarget;
    private DexConfig mConfig;
    private String mKey;
    private Theme mTheme;

    private boolean loadTargetActivity(Bundle savedInstanceState) {
        mKey = getIntent().getStringExtra(Constants.EXTRA_KEY);
        mConfig = DexManager.getClassLoader(mKey);
        if (null == mConfig) {
            return false;
        }

        try {
            String activityName = getIntent().getStringExtra(Constants.EXTRA_TARGET);
            Class<?> clz = mConfig.classLoader.loadClass(activityName);
            mTarget = (Activity) ReflectUtils.newInstance(clz);
            for (ActivityInfo activityInfo : mConfig.packageInfo.activities) {
                if (activityName.equals(activityInfo.name)) {
                    mTheme = mConfig.resources.newTheme();
                    mTheme.setTo(getTheme());
                    mTheme.applyStyle(0 == activityInfo.theme ?
                            0 == mConfig.packageInfo.applicationInfo.theme ?
                                    android.R.style.Theme_DeviceDefault :
                                    mConfig.packageInfo.applicationInfo.theme :
                            activityInfo.theme, true);
                }
            }
            clz.getMethod("intContext", Activity.class, String.class).invoke(mTarget, this, mConfig.packageInfo.packageName);
        } catch (Exception e) {
            Logger.e(e);
            return false;
        }

        try {
            ReflectUtils.invokeMethod(mTarget, "onCreate",
                    new Object[]{savedInstanceState}, new Class[]{Bundle.class});
        } catch (Exception e) {
            Logger.e(e);
            return false;
        }
        return true;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!loadTargetActivity(savedInstanceState)) {
            finish();
        }
    }

    private <T> T invokeMethod(String methodName, Object[] parameters, Class<?>[] parameterClzs) {
        try {
            return (T) ReflectUtils.invokeMethod(mTarget, methodName, parameters, parameterClzs);
        } catch (Exception e) {
            Logger.e(e);
        }
        return null;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        invokeMethod("onSaveInstanceState", new Object[]{outState}, new Class[]{Bundle.class});
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        invokeMethod("onRestoreInstanceState", new Object[]{savedInstanceState}, new Class[]{Bundle.class});
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        invokeMethod("onNewIntent", new Object[]{intent}, new Class[]{Intent.class});
        super.onNewIntent(intent);
    }

    @Override
    public void onBackPressed() {
        invokeMethod("onBackPressed", new Object[0], new Class[0]);
        super.onBackPressed();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        invokeMethod("onTouchEvent", new Object[]{event}, new Class[]{MotionEvent.class});
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        invokeMethod("onKeyUp", new Object[]{keyCode, event}, new Class[]{int.class, KeyEvent.class});
        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        invokeMethod("onDestroy", new Object[0], new Class[0]);
        super.onDestroy();
    }

    @Override
    public void onWindowAttributesChanged(WindowManager.LayoutParams params) {
        invokeMethod("onWindowAttributesChanged", new Object[]{params}, new Class[]{WindowManager.LayoutParams.class});
        super.onWindowAttributesChanged(params);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        invokeMethod("onWindowFocusChanged", new Object[]{hasFocus}, new Class[]{boolean.class});
        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        invokeMethod("onCreateOptionsMenu", new Object[]{menu}, new Class[]{Menu.class});
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        invokeMethod("onOptionsItemSelected", new Object[]{item}, new Class[]{MenuItem.class});
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        invokeMethod("onStart", new Object[0], new Class[0]);
        super.onStart();
    }

    @Override
    protected void onRestart() {
        invokeMethod("onRestart", new Object[0], new Class[0]);
        super.onRestart();
    }

    @Override
    protected void onResume() {
        invokeMethod("onResume", new Object[0], new Class[0]);
        super.onResume();
    }

    @Override
    protected void onPause() {
        invokeMethod("onPause", new Object[0], new Class[0]);
        super.onPause();
    }

    @Override
    protected void onStop() {
        invokeMethod("onStop", new Object[0], new Class[0]);
        super.onStop();
    }

    @Override
    public AssetManager getAssets() {
        return null == mConfig ? super.getAssets() : mConfig.assetManager;
    }

    @Override
    public Resources getResources() {
        return null == mConfig ? super.getResources() : mConfig.resources;
    }

    @Override
    public Theme getTheme() {
        return null == mTheme ? super.getTheme() : mTheme;
    }

    @Override
    public ClassLoader getClassLoader() {
        return mConfig.classLoader;
    }

    @Override
    public ComponentName startService(Intent service) {
        return DexManager.startService(this, mKey, service);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        DexManager.startActivityForResult(this, mKey, intent, requestCode);
    }
}
