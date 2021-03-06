package ask.op.sub.subdemo1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import ask.op.sdk.common.PluginUtils;

public class MainActivity extends Activity implements View.OnClickListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.simple).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        PluginUtils.startService(this, new Intent(this, MyService.class));
    }
}
