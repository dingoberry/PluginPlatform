package ask.op.sdk.host;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Window;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import ask.op.sdk.common.L;
import ask.op.sdk.common.ReflectUtils;

public class PluginHostActivity extends Activity {

    private static final boolean DEBUG = BuildConfig.DEBUG;
    private static final String TAG = "PluginHostActivity";

    private static Method sOnCreate = ReflectUtils.getMethod(Activity.class, "onCreate", Bundle.class);
    private static Method sOnPostCreate = ReflectUtils.getMethod(Activity.class, "onPostCreate", Bundle.class);
    private static Method sOnStart = ReflectUtils.getMethod(Activity.class, "onStart");
    private static Method sOnResume = ReflectUtils.getMethod(Activity.class, "onResume");
    private static Method sOnPostResume = ReflectUtils.getMethod(Activity.class, "onPostResume");
    private static Method sOnPause = ReflectUtils.getMethod(Activity.class, "onPause");
    private static Method sOnStop = ReflectUtils.getMethod(Activity.class, "onStop");
    private static Method sOnDestroy = ReflectUtils.getMethod(Activity.class, "onDestroy");
    private static Method sOnActivityResult = ReflectUtils.getMethod(Activity.class, "onActivityResult", Integer.TYPE, Integer.TYPE, Intent.class);

    private Activity mTargetActivity;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (null != sOnActivityResult && null != mTargetActivity) {
            try {
                sOnActivityResult.invoke(mTargetActivity, requestCode, resultCode, data);
            } catch (Exception e) {
                if (DEBUG) {
                    L.e(TAG, e);
                }
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = PluginManager.getIntent(this);
        if (null == intent) {
            finish();
            if (DEBUG) {
                L.i(TAG, "not found related Intent.");
            }
            finish();
            return;
        }

        PluginManager pm = PluginManager.getInstance(this);
        ComponentName cn = intent.getComponent();
        if (null == cn) {
            if (DEBUG) {
                L.i(TAG, "not found related ComponentName.");
            }
            finish();
            return;
        }

        PluginInfo info = pm.queryPluginInfo(cn.getPackageName());
        if (null == info) {
            if (DEBUG) {
                L.i(TAG, "plugin is not loaded.");
            }
            finish();
            return;
        }

        String activityName = cn.getClassName();
        ActivityInfo activityInfo = null;
        for (ActivityInfo temp : info.packageInfo.activities) {
            if (temp.name.equals(activityName)) {
                activityInfo = temp;
                break;
            }
        }
        if (null == activityInfo) {
            if (DEBUG) {
                L.i(TAG, "activity is not defined in plugin.");
            }
            finish();
            return;
        }

        try {
            Class<?> clz = info.classLoader.loadClass(activityInfo.name);
            Object targetActivity = clz.newInstance();
            if (!(targetActivity instanceof Activity)) {
                if (DEBUG) {
                    L.i(TAG, "Bad plugin activity=" + clz);
                }
                finish();
                return;
            }
            mTargetActivity = (Activity) targetActivity;
        } catch (Exception e) {
            if (DEBUG) {
                L.e(TAG, e);
            }
        }

        Context baseContext = getBaseContext();
        // Copy Platform activity all fields to plugin activity
        ReflectUtils.copyFields(Activity.class, this, mTargetActivity);
        // Replace plugin activity's mBase context, this will redirect mBase's resource, assetManager, classloader and so and to the Proxy Context
        replaceBaseContext(mTargetActivity, new PluginContext(info, baseContext));
        // Replace plugin activity's Theme, LayoutInflater, Resources, and the layoutInflater inside Windows
        replaceResources(mTargetActivity, info);
        // Replace the Window LifeCyler to plugin activity.
        replaceWindowCallback(mTargetActivity);
        // Replace mComponent, mApplicationInfo to the apkFile attr, and the title.
        replaceOthers(mTargetActivity, info, activityInfo);
        intent.setExtrasClassLoader(info.classLoader);
        mTargetActivity.setIntent(intent);

        if (null != sOnCreate) {
            try {
                sOnCreate.invoke(mTargetActivity, savedInstanceState);
                if (DEBUG) {
                    L.i(TAG, "Call Client OnCreat:" + mTargetActivity);
                }
            } catch (Exception e) {
                if (DEBUG) {
                    L.e(TAG, e);
                }
            }
        }

        mTargetActivity.setRequestedOrientation(activityInfo.screenOrientation);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (null != sOnPostCreate && null != mTargetActivity) {
            try {
                sOnPostCreate.invoke(mTargetActivity, savedInstanceState);
            } catch (Exception e) {
                if (DEBUG) {
                    L.e(TAG, e);
                }
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (null != sOnStart && null != mTargetActivity) {
            try {
                sOnStart.invoke(mTargetActivity);
            } catch (Exception e) {
                if (DEBUG) {
                    L.e(TAG, e);
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null != sOnResume && null != mTargetActivity) {
            try {
                sOnResume.invoke(mTargetActivity);
            } catch (Exception e) {
                if (DEBUG) {
                    L.e(TAG, e);
                }
            }
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (null != sOnPostResume && null != mTargetActivity) {
            try {
                sOnPostResume.invoke(mTargetActivity);
            } catch (Exception e) {
                if (DEBUG) {
                    L.e(TAG, e);
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (null != sOnPause && null != mTargetActivity) {
            try {
                sOnPause.invoke(mTargetActivity);
            } catch (Exception e) {
                if (DEBUG) {
                    L.e(TAG, e);
                }
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (null != sOnStop && null != mTargetActivity) {
            try {
                sOnStop.invoke(mTargetActivity);
            } catch (Exception e) {
                if (DEBUG) {
                    L.e(TAG, e);
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != sOnDestroy && null != mTargetActivity) {
            try {
                sOnDestroy.invoke(mTargetActivity);
            } catch (Exception e) {
                if (DEBUG) {
                    L.e(TAG, e);
                }
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void replaceOthers(Activity activity, PluginInfo info, ActivityInfo activityInfo) {
        try {
            ReflectUtils.getField(Activity.class, "mActivityInfo").set(activity, activityInfo);
        } catch (Exception e) {
            if (DEBUG) {
                L.e(TAG, e);
            }
        }
        try {
            ReflectUtils.getField(Activity.class, "mComponent").set(activity, new ComponentName(activityInfo.packageName, activityInfo.name));
        } catch (Exception e) {
            if (DEBUG) {
                L.e(TAG, e);
            }
        }
        try {
            Field field = ReflectUtils.getField(Activity.class, "mTitle");
            String title;
            if (null != activityInfo.nonLocalizedLabel) {
                title = activityInfo.nonLocalizedLabel.toString();
            } else if (0 != activityInfo.labelRes) {
                title = info.resources.getString(activityInfo.labelRes);
            } else if (null != activityInfo.name) {
                title = activityInfo.name;
            } else {
                title = activityInfo.packageName;
            }
            field.set(activity, title);
        } catch (Exception e) {
            if (DEBUG) {
                L.e(TAG, e);
            }
        }

        Window window = activity.getWindow();
        try {
            ReflectUtils.getField(window.getClass(), "mWindowStyle").set(window, null);
        } catch (Exception e) {
            if (DEBUG) {
                L.e(TAG, e);
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void replaceWindowCallback(Activity activity) {
        try {
            ReflectUtils.getField(Window.class, "mCallback").set(activity.getWindow(), activity);
        } catch (Exception e) {
            if (DEBUG) {
                L.e(TAG, e);
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void replaceResources(Activity activity, PluginInfo info) {
        try {
            ReflectUtils.getField(ContextThemeWrapper.class, "mInflater").set(activity, (Object) null);
            ReflectUtils.getField(ContextThemeWrapper.class, "mTheme").set(activity, (Object) null);
            ReflectUtils.getField(ContextThemeWrapper.class, "mResources").set(activity, info.resources);
        } catch (Exception e) {
            if (DEBUG) {
                L.e(TAG, e);
            }
        }

        if (0 != info.packageInfo.applicationInfo.theme) {
            int themeRes = info.packageInfo.applicationInfo.theme;
            activity.setTheme(themeRes);
            activity.getTheme().applyStyle(themeRes, true);
        }

        Window window = activity.getWindow();
        Field field = ReflectUtils.getField(window.getClass(), "mLayoutInflater");
        try {
            field.set(window, new PluginLayoutInflater((LayoutInflater) field.get(window), activity));
        } catch (IllegalAccessException e) {
            if (DEBUG) {
                L.e(TAG, e);
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void replaceBaseContext(Activity activity, Context newBase) {
        try {
            Field field = ReflectUtils.getField(activity.getClass(), "mBase");
            if (field.getType().isAssignableFrom(newBase.getClass())) {
                field.set(activity, newBase);
            }
        } catch (Exception e) {
            if (DEBUG) {
                L.e(TAG, e);
            }
        }
    }
}