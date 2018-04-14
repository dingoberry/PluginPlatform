package ask.op.pluginplatform;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ask.op.sdk.common.PkgUtils;
import ask.op.sdk.host.PluginManager;

public class MainActivity extends Activity {

    private ListView mList;
    private String[] mPluginNames;
    private List<String> mPkgNames;
    private InnerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mList = findViewById(R.id.main_list);

        AsyJob.post(new Runnable() {
            boolean runOnMain;

            @Override
            public void run() {
                if (runOnMain) {
                    mList.setAdapter(mAdapter);
                    mList.setOnItemClickListener(mAdapter);
                } else {
                    String[] pluginNames = getDir(Constants.PLUGIN_FOLDER, MODE_PRIVATE).list();
                    List<String> pkgNames = new ArrayList<>();
                    if (null != pluginNames) {
                        for (String pluginName : pluginNames) {
                            pkgNames.add(PluginManager.getInstance(MainActivity.this).register(new File(getDir(Constants.PLUGIN_FOLDER, MODE_PRIVATE), pluginName)
                                    .getAbsolutePath()));
                        }
                        mPluginNames = pluginNames;
                        mPkgNames = pkgNames;
                        mAdapter = new InnerAdapter();
                        runOnMain = true;
                        AsyJob.runOnUI(this);
                    }
                }
            }
        });
    }

    private void startPluginActivity(String pluginName, String pkgName, String activityName) {
        //        Pair<ActivityInfo, IntentFilter[]> pair =
//                PkgUtils.queryMainLaunchActivityInfo(this, new File(getDir(Constants.PLUGIN_FOLDER, MODE_PRIVATE), pluginName));
//        Logger.i(pair.toString());

//                DexClassLoader loader = new DexClassLoader(new File(getDir(Constants.PLUGIN_FOLDER, MODE_PRIVATE), pluginName)
//                .getAbsolutePath(), getFilesDir().getAbsolutePath(), null, getClassLoader());

        if (TextUtils.isEmpty(activityName)) {
            PackageInfo info = PkgUtils.getPackageInfo(this,
                    new File(getDir(Constants.PLUGIN_FOLDER, MODE_PRIVATE), pluginName).getAbsolutePath());
            if (null != info.activities) {
                activityName = info.activities[0].name;
            } else {
                return;
            }
        }
//
//        Intent intent = new Intent(this, ProxyActivity.class);
//        intent.putExtra(Constants.EXTRA_TARGET, activityName);
//        intent.putExtra(Constants.EXTRA_KEY, pluginName);
//        startActivity(intent);

        Intent intent = new Intent();
        intent.setClassName(pkgName, activityName);
        PluginManager.startActivity(this, intent);
    }

    private class InnerAdapter extends BaseAdapter implements AdapterView.OnItemClickListener {

        @Override
        public int getCount() {
            return mPluginNames.length;
        }

        @Override
        public Object getItem(int position) {
            return mPluginNames[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (null == convertView) {
                convertView = getLayoutInflater().inflate(android.R.layout.simple_list_item_1, null);
            }
            TextView t = convertView.findViewById(android.R.id.text1);
            t.setText(getItem(position).toString());
            return convertView;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            startPluginActivity(getItem(position).toString(), mPkgNames.get(position), null);
        }
    }
}
