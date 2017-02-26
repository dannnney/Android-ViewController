package io.hytron.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.util.AndroidRuntimeException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by danney on 16/1/18.
 */
public abstract class ViewController {
    static final int INITIALIZING = 0;     // Not yet created.
    static final int CREATED = 1;          // Created.
    static final int STOPPED = 3;          // Fully created, not started.
    static final int STARTED = 4;          // Created and started, not resumed.
    static final int RESUMED = 5;          // Created started and resumed.
    protected View view;
    protected Bundle props;
    protected ViewControllerManager viewControllerManager;
    int state = INITIALIZING;
    int containerViewId = 0;
    String tag;
    private Context context;
    private Boolean called = false;
    private ViewControllerManager.ViewControllerHostCallback hostCallback = new ViewControllerManager.ViewControllerHostCallback() {
        @Override
        public View findViewById(int viewId) {
            return view.findViewById(viewId);
        }

        @Override
        public Context getContext() {
            return ViewController.this.getContext();
        }

        @Override
        public Bundle getProps() {
            return props;
        }
    };

    public ViewController() {
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Bundle getProps() {
        return props;
    }

    public void setProps(Bundle props) {
        this.props = props;
    }

    public ViewControllerManager getViewControllerManager() {
        if (viewControllerManager == null) {
            viewControllerManager = new ViewControllerManager();
            viewControllerManager.addHost(hostCallback);
        }
        return viewControllerManager;
    }

    @Nullable
    public View getView() {
        return view;
    }

    public void finish() {
        ((Activity) getContext()).finish();
    }

    //以下为传递Activity的事件
    public void onCreate(Bundle savedInstanceState) {
        called = true;
    }

    public abstract View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    public void onStart() {
        called = true;
    }

    public void onResume() {
        called = true;
    }

    public void onNewIntent(Intent intent) {
        called = true;
    }

    public void onPause() {
        called = true;
    }

    public void onStop() {
        called = true;
    }

    public void onDestroy() {
        called = true;
    }

    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        called = true;
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        called = true;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        called = true;
    }

    public void onConfigurationChanged(Configuration newConfig) {
        called = true;
    }

    public void onLowMemory() {
        called = true;
    }

    public boolean onBackPressed() {
        return false;
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    }

    void performCreate(Bundle savedInstanceState) {
        called = false;
        onCreate(savedInstanceState);

        if (!called) {
            throw new AndroidRuntimeException("ViewController " + this
                    + " did not call through to super.onCreate()");
        }
    }

    View performCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        view = onCreateView(inflater, container, savedInstanceState);
        state = ViewController.CREATED;
        return view;
    }

    void performStart() {
        called = false;
        state = STARTED;
        onStart();
        if (!called) {
            throw new AndroidRuntimeException("ViewController " + this
                    + " did not call through to super.onStart()");
        }
        if (viewControllerManager != null) {
            viewControllerManager.dispatchStart();
        }
    }

    void performResume() {
        called = false;
        state = RESUMED;
        onResume();
        if (!called) {
            throw new AndroidRuntimeException("ViewController " + this
                    + " did not call through to super.onResume()");
        }
        if (viewControllerManager != null) {
            viewControllerManager.dispatchResume();
        }
    }

    void performNewIntent(Intent intent) {
        called = false;
        //state = RESUMED;
        onNewIntent(intent);
        if (!called) {
            throw new AndroidRuntimeException("ViewController " + this
                    + " did not call through to super.onNewIntent()");
        }
        if (viewControllerManager != null) {
            viewControllerManager.dispatchNewIntent(intent);
        }
    }

    void performPause() {
        state = STARTED;
        if (viewControllerManager != null) {
            viewControllerManager.dispatchPause();
        }
        called = false;
        onPause();
        if (!called) {
            throw new AndroidRuntimeException("ViewController " + this
                    + " did not call through to super.onPause()");
        }
    }

    void performStop() {
        state = STOPPED;
        if (viewControllerManager != null) {
            viewControllerManager.dispatchStop();
        }
        called = false;
        onStop();
        if (!called) {
            throw new AndroidRuntimeException("ViewController " + this
                    + " did not call through to super.onStop()");
        }
    }

    void performDestroy() {
        if (viewControllerManager != null) {
            viewControllerManager.dispatchDestroy();
            viewControllerManager = null;
        }
        called = false;
        onDestroy();
        if (!called) {
            throw new AndroidRuntimeException("ViewController " + this
                    + " did not call through to super.onDestroy()");
        }
    }

    void performSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        if (viewControllerManager != null) {
            viewControllerManager.dispatchSaveInstanceState(outState, outPersistentState);
        }
        called = false;
        onSaveInstanceState(outState, outPersistentState);
        if (!called) {
            throw new AndroidRuntimeException("ViewController " + this
                    + " did not call through to super.onSaveInstanceState()");
        }
    }

    void performRestoreInstanceState(Bundle savedInstanceState) {
        if (viewControllerManager != null) {
            viewControllerManager.dispatchRestoreInstanceState(savedInstanceState);
        }
        called = false;
        onRestoreInstanceState(savedInstanceState);
        if (!called) {
            throw new AndroidRuntimeException("ViewController " + this
                    + " did not call through to super.onRestoreInstanceState()");
        }
    }

    void performActivityResult(int requestCode, int resultCode, Intent data) {
        onActivityResult(requestCode, resultCode, data);
        if (viewControllerManager != null) {
            viewControllerManager.dispatchActivityResult(requestCode, resultCode, data);
        }
    }

    void performConfigurationChanged(Configuration newConfig) {
        onConfigurationChanged(newConfig);
        if (viewControllerManager != null) {
            viewControllerManager.dispatchConfigurationChanged(newConfig);
        }
    }

    void performLowMemory() {
        onLowMemory();
        if (viewControllerManager != null) {
            viewControllerManager.dispatchLowMemory();
        }
    }

    boolean performBackPressed() {
        if (viewControllerManager != null) {
            if (viewControllerManager.dispatchBackPressed()) {
                return true;
            }
        }
        return onBackPressed();
    }

    void performRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (viewControllerManager != null) {
            viewControllerManager.dispatchRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}