package io.hytron.demo.viewController;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.hytron.base.ViewController;
import io.hytron.demo.R;

/**
 * Created by danney on 16/3/4.
 */
public class TopViewController extends ViewController {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.vc_top, container);
    }
}