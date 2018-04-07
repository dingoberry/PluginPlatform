package ask.op.plugin;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

@SuppressLint("MissingSuperCall")
public abstract class OpPluginActivity extends Activity {

    private Activity mProxy;
    private String mPkgName;

    @SuppressWarnings("unused")
    public void intContext(Activity proxy, String pkgName) {
        mProxy = proxy;
        mPkgName = pkgName;
    }

    @Override
    public String getPackageName() {
        return mPkgName;
    }

    @Override
    public Intent getIntent() {
        return mProxy.getIntent();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
    }

    @Override
    public void setContentView(int layoutResID) {
        mProxy.setContentView(layoutResID);
    }

    @Override
    public void setContentView(View view) {
        mProxy.setContentView(view);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        mProxy.setContentView(view, params);
    }

    @Override
    public void addContentView(View view, ViewGroup.LayoutParams params) {
        mProxy.addContentView(view, params);
    }

    @Override
    public <T extends View> T findViewById(int id) {
        return mProxy.findViewById(id);
    }

    @Override
    public ClassLoader getClassLoader() {
        return mProxy.getClassLoader();
    }

    @Override
    public Resources getResources() {
        return mProxy.getResources();
    }

    @Override
    public LayoutInflater getLayoutInflater() {
        return mProxy.getLayoutInflater();
    }

    @Override
    public MenuInflater getMenuInflater() {
        return mProxy.getMenuInflater();
    }

    @Override
    public SharedPreferences getSharedPreferences(String name, int mode) {
        return mProxy.getSharedPreferences(name, mode);
    }

    @Override
    public ApplicationInfo getApplicationInfo() {
        return mProxy.getApplicationInfo();
    }

    @Override
    public WindowManager getWindowManager() {
        return mProxy.getWindowManager();
    }

    @Override
    public Window getWindow() {
        return mProxy.getWindow();
    }

    @Override
    public Object getSystemService(String name) {
        return mProxy.getSystemService(name);
    }

    @Override
    public void finish() {
        mProxy.finish();
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onRestart() {

    }

    @Override
    public void onDestroy() {
    }

    @Override
    public void onResume() {
    }

    @Override
    public void onPause() {
    }

    @Override
    public void onStop() {

    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
    }

    @Override
    public void onNewIntent(Intent intent) {

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public void onWindowAttributesChanged(WindowManager.LayoutParams params) {

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        mProxy.startActivityForResult(intent, requestCode);
    }

    @Override
    public ComponentName startService(Intent service) {
        return mProxy.startService(service);
    }

    @Override
    public boolean stopService(Intent name) {
        return mProxy.stopService(name);
    }

    @Override
    public boolean bindService(Intent service, ServiceConnection conn, int flags) {
        return mProxy.bindService(service, conn, flags);
    }

    @Override
    public void unbindService(ServiceConnection conn) {
        mProxy.unbindService(conn);
    }
}
