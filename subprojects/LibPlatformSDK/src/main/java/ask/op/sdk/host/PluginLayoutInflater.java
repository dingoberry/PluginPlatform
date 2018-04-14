package ask.op.sdk.host;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import java.lang.reflect.Constructor;
import java.util.HashMap;

import ask.op.sdk.common.L;

class PluginLayoutInflater extends LayoutInflater {

    private static final boolean DEBUG = BuildConfig.DEBUG;
    private static final String TAG = "PluginLayoutInflater";

    private static final String[] a = new String[]{"android.widget.", "android.webkit.", "android.app."};
    private static final Class<?>[] mViewParameterTypes = new Class[]{Context.class, AttributeSet.class};
    private final ClassLoader mCloassLoader;
    private final HashMap<String, Constructor<? extends View>> mViewConstructors;

    public PluginLayoutInflater(Context cxt) {
        super(cxt);
        mCloassLoader = getContext().getClassLoader();
        mViewConstructors = new HashMap<>();
        this.setFactory(new InnerFactory());
    }

    public PluginLayoutInflater(LayoutInflater layoutInflater, Context cxt) {
        super(layoutInflater, cxt);
        mCloassLoader = getContext().getClassLoader();
        if (layoutInflater instanceof PluginLayoutInflater) {
            this.mViewConstructors = ((PluginLayoutInflater) layoutInflater).mViewConstructors;
        } else {
            this.mViewConstructors = new HashMap<>();
        }

        Factory factory = this.getFactory();
        if (null == factory || !(factory instanceof InnerFactory)) {
            this.setFactory(new InnerFactory());
        }

    }

    @Override
    public LayoutInflater cloneInContext(Context newContext) {
        return new PluginLayoutInflater(this, newContext);
    }

    private class InnerFactory implements Factory {

        @Override
        public View onCreateView(String name, Context context, AttributeSet attrs) {
            if (name.indexOf(46) != -1) {
                return makeView(name, context, attrs);
            } else {
                return null;
            }
        }

        private View makeView(String name, Context context, AttributeSet attrs) {
            try {
                Constructor<? extends View> constructor = mViewConstructors.get(name);
                if (null == constructor) {
                    Class<? extends View> clz = mCloassLoader.loadClass(name).asSubclass(View.class);
                    constructor = clz.getConstructor(mViewParameterTypes);
                    constructor.setAccessible(true);
                    mViewConstructors.put(name, constructor);
                }
                return constructor.newInstance(context, attrs);
            } catch (Exception e) {
                if (DEBUG) {
                    L.e(TAG, e);
                }
            }
            return null;
        }
    }
}
