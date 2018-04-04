package ask.op.pluginplatform;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import dalvik.system.DexClassLoader;

/**
 * Created by tf on 4/3/2018.
 */

public class ProxyActivity extends Activity {

    public static final String EXTRA_ACTIVITY = "ex.a";
    public static final String EXTRA_KEY = "ex.k";

    private Activity mTarget;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DexClassLoader loader = DexManager.getClassLoader(getIntent().getStringExtra(EXTRA_KEY));

        try {
            Class<?> clz = loader.loadClass(getIntent().getStringExtra(EXTRA_ACTIVITY));
            mTarget = (Activity) ReflectUtils.newInstance(clz);
        } catch (Exception e) {
            Logger.e(e);
            finish();
            return;
        }

        try {
            ReflectUtils.invokeMethod(mTarget, "onCreate",
                    new Object[]{savedInstanceState}, new Class[]{Bundle.class});
        } catch (Exception e) {
            Logger.e(e);
        }
    }
}
