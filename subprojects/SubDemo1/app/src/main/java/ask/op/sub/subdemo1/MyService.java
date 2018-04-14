package ask.op.sub.subdemo1;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import ask.op.sdk.common.L;

public class MyService extends Service {
    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        L.e("yymm", "HELLO from service!");
        return super.onStartCommand(intent, flags, startId);
    }
}
