package ask.op.plugin;

import android.app.Activity;
import android.app.Service;

public abstract class OpPluginService extends Service{

    private Service mProxy;
    private String mPkgName;

    @SuppressWarnings("unused")
    public void intContext(Activity proxy, String pkgName) {
         
        mPkgName = pkgName;
    }
}
