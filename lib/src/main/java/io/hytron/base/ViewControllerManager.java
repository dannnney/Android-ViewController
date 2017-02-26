package io.hytron.base;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by danney on 16/1/18.
 */
public class ViewControllerManager {
    private static final int MSG_ADD = 0;
    private static final int MSG_MOVE_TO_STARTED = 1;
    int state = ViewController.INITIALIZING;
    /*
    * ViewControllerMgr的持有者
    * */
    ViewControllerHostCallback host;

    /*
    *
    * 等待被添加的ViewController
    * */
    ArrayList<ViewController> addingList;


    /*
    *
    * 已经被添加到Mgr的ViewController
    * */
    ArrayList<ViewController> addedList;
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_ADD: {
                    addSubViewContainers();
                    break;
                }
            }
            super.handleMessage(msg);
        }
    };

    public ViewControllerManager() {
        addedList = new ArrayList<ViewController>();
    }

    /**
     * 将一个ViewController添加到管理器中，这样vc才能接受到所有的Activity的生命周期事件
     */
    public void add(int containerViewId, ViewController vc, String tag, boolean isSync) {
        if (vc == null) {
            throw new RuntimeException("ViewController can not be null");
        }

        if (addingList == null) {
            addingList = new ArrayList<ViewController>();
        }

        vc.containerViewId = containerViewId;
        vc.tag = tag;
        vc.setContext(host.getContext());
        vc.setProps(host.getProps());
        vc.performCreate(null);

        if (isSync) {
            LayoutInflater inflater = LayoutInflater.from(host.getContext());
            addSubViewContainer(inflater, vc);
        } else {
            addingList.add(vc);
            handler.sendEmptyMessage(MSG_ADD);
        }
    }

    public void add(ViewController vc, String tag, boolean isSync) {
        add(0, vc, tag, isSync);
    }

    public void add(int containerViewId, ViewController vc) {
        add(containerViewId, vc, null, false);
    }

    /**
     * 添加ViewControllerMgr的持有者callback，用于从ViewControllerMgr中来调用持有者的一些接口
     */
    public void addHost(ViewControllerHostCallback host) {
        this.host = host;
    }

    public ViewController findViewControllerByTag(String tag) {
        if (tag != null && tag.length() > 0) {
            for (int i = 0; i < addedList.size(); ++i) {
                String vcTag = addedList.get(i).tag;
                if (tag.compareTo(vcTag) == 0) {
                    return addedList.get(i);
                }
            }
        }
        return null;
    }

    public void dispatchCreate() {
        this.state = ViewController.CREATED;
    }

    public void dispatchStart() {
        this.state = ViewController.STARTED;
        if (addedList != null) {
            for (int i = 0; i < addedList.size(); i++) {
                ViewController vc = addedList.get(i);
                if (vc != null) {
                    vc.performStart();
                }
            }
        }
    }

    public void dispatchResume() {
        this.state = ViewController.RESUMED;
        if (addedList != null) {
            for (int i = 0; i < addedList.size(); i++) {
                ViewController vc = addedList.get(i);
                if (vc != null) {
                    vc.performResume();
                }
            }
        }
    }

    public void dispatchNewIntent(Intent intent) {
        //this.state = ViewController.RESUMED;
        if (addedList != null) {
            for (int i = 0; i < addedList.size(); i++) {
                ViewController vc = addedList.get(i);
                if (vc != null) {
                    vc.performNewIntent(intent);
                }
            }
        }
    }

    public void dispatchPause() {
        this.state = ViewController.STARTED;
        if (addedList != null) {
            for (int i = 0; i < addedList.size(); i++) {
                ViewController vc = addedList.get(i);
                if (vc != null) {
                    vc.performPause();
                }
            }
        }
    }

    public void dispatchStop() {
        this.state = ViewController.STOPPED;
        if (addedList != null) {
            for (int i = 0; i < addedList.size(); i++) {
                ViewController vc = addedList.get(i);
                if (vc != null) {
                    vc.performStop();
                }
            }
        }
    }

    public void dispatchDestroy() {
        if (addedList != null) {
            for (int i = 0; i < addedList.size(); i++) {
                ViewController vc = addedList.get(i);
                if (vc != null) {
                    vc.performDestroy();
                }
            }
        }
    }

    public void dispatchSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        if (addedList != null) {
            for (int i = 0; i < addedList.size(); i++) {
                ViewController vc = addedList.get(i);
                if (vc != null) {
                    vc.performSaveInstanceState(outState, outPersistentState);
                }
            }
        }
    }

    public void dispatchRestoreInstanceState(Bundle savedInstanceState) {
        if (addedList != null) {
            for (int i = 0; i < addedList.size(); i++) {
                ViewController vc = addedList.get(i);
                if (vc != null) {
                    vc.performRestoreInstanceState(savedInstanceState);
                }
            }
        }
    }

    public void dispatchActivityResult(int requestCode, int resultCode, Intent data) {
        if (addedList != null) {
            for (int i = 0; i < addedList.size(); i++) {
                ViewController vc = addedList.get(i);
                if (vc != null) {
                    vc.performActivityResult(requestCode, resultCode, data);
                }
            }
        }
    }

    public void dispatchConfigurationChanged(Configuration newConfig) {
        if (addedList != null) {
            for (int i = 0; i < addedList.size(); i++) {
                ViewController vc = addedList.get(i);
                if (vc != null) {
                    vc.performConfigurationChanged(newConfig);
                }
            }
        }
    }

    public void dispatchLowMemory() {
        if (addedList != null) {
            for (int i = 0; i < addedList.size(); i++) {
                ViewController vc = addedList.get(i);
                if (vc != null) {
                    vc.performLowMemory();
                }
            }
        }
    }

    public boolean dispatchBackPressed() {
        if (addedList != null) {
            for (int i = 0; i < addedList.size(); i++) {
                ViewController vc = addedList.get(i);
                if (vc != null) {
                    if (vc.performBackPressed()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void dispatchRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (addedList != null) {
            for (int i = 0; i < addedList.size(); i++) {
                ViewController vc = addedList.get(i);
                if (vc != null) {
                    vc.performRequestPermissionsResult(requestCode, permissions, grantResults);
                }
            }
        }
    }

    private void addSubViewContainers() {
        LayoutInflater inflater = LayoutInflater.from(host.getContext());
        int size = addingList.size();
        for (int i = 0; i < size; ++i) {
            if (host == null) {
                throw new RuntimeException("there is no host callback in ViewControllerManager");
            }


            ViewController vc = addingList.get(i);
            addSubViewContainer(inflater, vc);
        }
        addingList.clear();
    }

    private void addSubViewContainer(LayoutInflater inflater, final ViewController vc) {
        ViewGroup containerView = null;
        if (vc.containerViewId > 0) {
            containerView = (ViewGroup) host.findViewById(vc.containerViewId);
        }

        vc.performCreateView(inflater, containerView, null);
        addedList.add(vc);

        if (this.state > vc.state) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    vc.performStart();
                }
            });
        }
    }

    /*
    * 外层持有者实现
    * */
    public interface ViewControllerHostCallback {
        View findViewById(int viewId);

        Context getContext();

        Bundle getProps();
    }
}