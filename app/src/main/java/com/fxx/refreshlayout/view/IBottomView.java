package com.fxx.refreshlayout.view;

import android.view.View;


/**
 * footer视图接口
 */
public interface IBottomView {
    View getView();


    /**
     * 上拉过程
     * @param fraction 上拉高度和footer高度之比
     * @param maxFooterHeight footer可拉伸最大高度
     * @param footerHeight footer高度
     */
    void onPullingUp(float fraction, float maxFooterHeight, float footerHeight);

    void startAnim(float maxFooterHeight, float footerHeight);


    /**
     * 上拉释放过程
     * @param fraction
     * @param maxFooterHeight
     * @param footerHeight
     */
    void onPullReleasing(float fraction, float maxFooterHeight, float footerHeight);

    void onFinish();

    void reset();
}
