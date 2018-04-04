package ask.op.pluginplatform;

import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;

import java.io.File;
import java.io.IOException;

public class MyApp extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        AsyJob.init();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        AsyJob.post(new Runnable() {
            @Override
            public void run() {
                try {
                    AssetManager am = getResources().getAssets();
                    String[] files = am.list(Constants.PLUGIN_FOLDER);
                    for (String file : files) {
                        OsUtils.copy(am.open(Constants.PLUGIN_FOLDER + File.separator + file, MODE_PRIVATE),
                                new File(getDir(Constants.PLUGIN_FOLDER, MODE_PRIVATE), file).getAbsolutePath());
                    }
                } catch (IOException e) {
                    Logger.e(e);
                }
            }
        });
    }
}
