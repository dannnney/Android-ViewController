package io.hytron.demo.activity;

import android.os.Bundle;

import io.hytron.base.ViewControllerActivity;
import io.hytron.demo.viewController.FirstViewController;

public class MainActivity extends ViewControllerActivity {
    FirstViewController firstViewController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firstViewController = new FirstViewController();
        getViewControllerManager().add(firstViewController, null, true);
        setContentView(firstViewController.getView());
    }
}