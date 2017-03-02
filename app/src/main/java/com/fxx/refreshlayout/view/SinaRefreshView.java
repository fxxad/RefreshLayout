package com.fxx.refreshlayout.view;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.fxx.refreshlayout.R;


/**
 * 仿新浪下拉刷新视图
 */
public class SinaRefreshView extends FrameLayout implements IHeaderView {

    /**
     * UI
     */
    private ImageView mRefreshArrow;
    private ImageView loadingView;
    private TextView refreshTextView;

    private String pullDownStr = "下拉刷新";
    private String releaseRefreshStr = "释放更新";
    private String refreshingStr = "正在刷新";

    public SinaRefreshView(Context context) {
        this(context, null);
    }

    public SinaRefreshView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SinaRefreshView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * 初始化view
     */
    private void init() {
        View rootView = View.inflate(getContext(), R.layout.view_sinaheader, null);
        mRefreshArrow = (ImageView) rootView.findViewById(R.id.iv_arrow);
        refreshTextView = (TextView) rootView.findViewById(R.id.tv);
        loadingView = (ImageView) rootView.findViewById(R.id.iv_loading);
        addView(rootView);
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void onPullingDown(float fraction, float maxHeadHeight, float headHeight) {
        if (fraction < 1f) {
            refreshTextView.setText(pullDownStr);
            mRefreshArrow.setRotation(0);
        }

        if (fraction > 1f) {
            refreshTextView.setText(releaseRefreshStr);
//            refreshArrow.setRotation(fraction * headHeight / maxHeadHeight * 180);
            mRefreshArrow.setRotation(180);
        }
    }

    @Override
    public void onPullReleasing(float fraction, float maxHeadHeight, float headHeight) {
        if (fraction < 1f) {
            refreshTextView.setText(pullDownStr);
//            refreshArrow.setRotation(fraction * headHeight / maxHeadHeight * 180);
            mRefreshArrow.setRotation(0);
            if (mRefreshArrow.getVisibility() == GONE) {
                mRefreshArrow.setVisibility(VISIBLE);
                loadingView.setVisibility(GONE);
            }
        }
    }

    /**
     * 开始加载动画
     * @param maxHeadHeight
     * @param headHeight
     */
    @Override
    public void startAnim(float maxHeadHeight, float headHeight) {
        refreshTextView.setText(refreshingStr);
        mRefreshArrow.setVisibility(GONE);
        loadingView.setVisibility(VISIBLE);
        ((AnimationDrawable) loadingView.getDrawable()).start();
    }

    @Override
    public void onFinish(OnAnimEndListener listener) {
        listener.onAnimEnd();
    }

    /**
     * 重置
     */
    @Override
    public void reset() {
        mRefreshArrow.setVisibility(VISIBLE);
        loadingView.setVisibility(GONE);
        refreshTextView.setText(pullDownStr);
    }

    /**
     * 设置箭头资源
     * @param resId 箭头资源
     */
    public void setArrowResource(@DrawableRes int resId) {
        mRefreshArrow.setImageResource(resId);
    }

    /**
     * 设置文字颜色
     * @param color 文字颜色
     */
    public void setTextColor(@ColorInt int color) {
        refreshTextView.setTextColor(color);
    }

    /**
     * 设置下拉准备过程的文字
     * @param pullDownStr 下拉准备的文字
     */
    public void setPullDownStr(String pullDownStr) {
        this.pullDownStr = pullDownStr;
    }

    /**
     * 设置下拉完成的文字
     * @param releaseRefreshStr 下拉完成的文字
     */
    public void setReleaseRefreshStr(String releaseRefreshStr) {
        this.releaseRefreshStr = releaseRefreshStr;
    }

    /**
     * 设置刷新过程文字
     * @param refreshingStr 刷新过程文字
     */
    public void setRefreshingStr(String refreshingStr) {
        this.refreshingStr = refreshingStr;
    }

}
