package io.hytron.base;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by danney on 16/1/27.
 */
public abstract class ViewControllerPageAdapter extends PagerAdapter {
    private ViewControllerManager viewControllerMgr;

    public ViewControllerPageAdapter(ViewControllerManager vcm) {
        viewControllerMgr = vcm;
    }

    public abstract ViewController getItem(int position);

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        String tag = makeTag(container.getId(), position);
        ViewController vc = viewControllerMgr.findViewControllerByTag(tag);
        if (vc != null) {
            container.addView(vc.getView());
        } else {
            vc = getItem(position);
            viewControllerMgr.add(container.getId(), vc, tag, false);
        }

        return vc;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(((ViewController) object).getView());
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return ((ViewController) object).getView() == view;
    }

    private String makeTag(int viewId, long id) {
        return "android:switcher:" + viewId + ":" + id;
    }
}