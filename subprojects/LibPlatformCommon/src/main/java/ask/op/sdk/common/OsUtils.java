package ask.op.sdk.common;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class OsUtils {
    private static final boolean DEBUG = BuildConfig.DEBUG;
    private static final String TAG = "OsUtils";

    public static void close(Closeable c) {
        if (null == c) {
            return;
        }

        try {
            c.close();
        } catch (IOException e) {
            if (DEBUG) {
                L.e(TAG, e);
            }
        }
    }

    public static void copy(InputStream ins, String des) {
        File file = new File(des);
        File folder = file.getParentFile();
        if (null != folder && !folder.exists()) {
            if (folder.mkdirs()) {
                return;
            }
        }

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            byte[] buffer = new byte[2048];
            int len;
            while (-1 != (len = ins.read(buffer))) {
                fos.write(buffer, 0, len);
            }
            fos.flush();
        } catch (IOException e) {
            if (DEBUG) {
                L.e(TAG, e);
            }
        } finally {
            close(fos);
            close(ins);
        }
    }
}
