package com.fxx.refreshlayout.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.fxx.refreshlayout.R;
import com.fxx.refreshlayout.util.DensityUtil;


/**
 * Created by mc on 2017/2/28.
 * 下拉刷新、上拉加载动画
 */

public class RefreshLayout extends RelativeLayout {
    private static final  int PUT_TOP_DOWN=0x01;//下拉刷新状态
    private static final int PUT_BOTTOM_UP=0x02;//上拉加载状态
    private static final float mAnimFraction = 1f;

    //是否正在刷新标志
    private boolean isRefreshing=false;
    //是否正在加载标志
    private boolean isLoading=false;

    //动画插值器
    private DecelerateInterpolator mDecelerateInterpolator;

    //刷新、加载监听
    private IPullListener mPullListener;

    //用于记录是刷新状态还是加载状态
    private int state=0;

    //点击事件down时的坐标点的x和y坐标
    private float mDownX;
    private float mDownY;

    //头视图高度
    private int mHeaderHeight;
    //底部视图高度
    private int mFooterHeight;

    //子view
    private View mChildView;

    private FrameLayout mHeaderLayout;
    //顶部头view
    private IHeaderView mHeaderView;
    private FrameLayout mFooterLayout;
    //底部bottomview
    private IBottomView mFooterView;

    public RefreshLayout(Context context) {
        this(context,null,0);
    }

    public RefreshLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public RefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        //获取属性
        TypedArray typedArray=context.obtainStyledAttributes(attrs,
                R.styleable.RefreshLayout,defStyleAttr,0);
        mHeaderHeight = typedArray.getDimensionPixelSize(
                R.styleable.RefreshLayout_header_height, DensityUtil.dp2px(context,60));
        mFooterHeight=typedArray.getDimensionPixelSize(R.styleable.RefreshLayout_footer_height,
                DensityUtil.dp2px(context,60));
        typedArray.recycle();
        init();
    }

    /**
     * 初始化
     */
    private void init(){
        //初始化动画插值器
        mDecelerateInterpolator =new DecelerateInterpolator(8);
    }


    @Override
    protected void onAttachedToWindow() {
        mChildView=getChildAt(0);
        // 添加headerview
        if(mHeaderLayout==null){
            mHeaderLayout=new FrameLayout(getContext());
            RelativeLayout.LayoutParams params=new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    0);
            params.addRule(ALIGN_PARENT_TOP);
            params.addRule(CENTER_VERTICAL);

            addView(mHeaderLayout,params);

            mHeaderLayout.setVisibility(View.GONE);

            if(mHeaderView==null){
                setmHeaderView(new SinaRefreshView(getContext()));
            }
        }

        //添加footerview
        if(mFooterLayout==null){
            mFooterLayout=new FrameLayout(getContext());
            RelativeLayout.LayoutParams params2=new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    0);
            params2.addRule(ALIGN_PARENT_BOTTOM);
            params2.addRule(CENTER_VERTICAL);

            addView(mFooterLayout,params2);

            mFooterLayout.setVisibility(View.GONE);

            if(mFooterView==null){
                setmFooterView(new LoadingView(getContext()));
            }
        }
        super.onAttachedToWindow();
    }

    /**
     * 用于处理是否拦截触摸事件,返回true表示拦截则接着回调onTouchEvent消费触摸事件，
     * 否则不拦截，触摸事件接着向下级view传递
     * @param ev 触摸事件
     * @return true／false
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(mChildView == null){
            return super.onInterceptTouchEvent(ev);
        }
        boolean intercept=false;
        if(isRefreshing || isLoading){
            intercept=true;
        }else {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mDownX = ev.getX();
                    mDownY = ev.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    //滑动过程中的点坐标
                    float dx = ev.getX() - mDownX;
                    float dy = ev.getY() - mDownY;
                    if (Math.abs(dx) <= Math.abs(dy)) {//滑动的最大角度为45度
                        if (dy > 0 && !ViewCompat.canScrollVertically(mChildView, -1)) {//下拉刷新
                            intercept = true;
                            state = PUT_TOP_DOWN;
                        } else if (dy < 0 && !ViewCompat.canScrollVertically(mChildView, 1)) {//上拉加载
                            intercept = true;
                            state = PUT_BOTTOM_UP;
                        }
                    }
                    break;
            }
        }
        return intercept || super.onInterceptTouchEvent(ev);
    }

    /**
     * 处理触摸事件，返回true表示消费了触摸事件，
     *  否则表示未处理，则触摸事件抛向父级控件处理
     * @param event 触摸事件
     * @return true／false
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean consume = false;
        if(isRefreshing || isLoading){
            consume=true;
        }else {
            switch (event.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    float dy = event.getY() - mDownY;
                    if (state == PUT_TOP_DOWN) {
                        //显示下拉刷新视图
                        dy = Math.min(mHeaderHeight * 2, dy);
                        dy = Math.max(0, dy);
                        dealPullDown(dy);
                    } else if (state == PUT_BOTTOM_UP) {
                        //显示上拉加载视图
                        dy = Math.min(mFooterHeight * 2, Math.abs(dy));
                        dy = Math.max(0, dy);
                        dealPullUp(dy);
                    }
                    consume = true;
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    if (state == PUT_TOP_DOWN) {
                        //显示正在刷新视图 并回调刷新接口
                        dealPullDownRelease();
                    } else if (state == PUT_BOTTOM_UP) {
                        //显示正在加载视图，并回调加载更多接口
                        dealPullUpRelease();
                    }
                    consume = true;
                    break;
            }
        }
        return consume || super.onTouchEvent(event);
    }

    /**
     * 处理下拉过程
     * @param dy y轴移动距离
     */
    private void dealPullDown(float dy){
        float offsetY = mDecelerateInterpolator.getInterpolation(dy / mHeaderHeight / 2) * dy / 2;

        if (mHeaderLayout.getVisibility() != VISIBLE) mHeaderLayout.setVisibility(VISIBLE);

        mHeaderLayout.getLayoutParams().height = (int) Math.abs(offsetY);
        mHeaderLayout.requestLayout();
        mChildView.setTranslationY(offsetY);
        mHeaderView.onPullingDown(dy/mHeaderHeight,mHeaderHeight,mHeaderHeight);
    }

    /**
     * 处理上拉过程
     * @param dy y轴移动距离
     */
    private void dealPullUp(float dy){
        float offsetY = mDecelerateInterpolator.getInterpolation(dy / mFooterHeight / 2) * dy / 2;

        if (mFooterLayout.getVisibility() != VISIBLE) mFooterLayout.setVisibility(VISIBLE);

        mFooterLayout.getLayoutParams().height = (int) Math.abs(offsetY);
        mFooterLayout.requestLayout();
        mChildView.setTranslationY(-offsetY);
        mFooterView.onPullingUp(dy/mFooterHeight,mFooterHeight,mFooterHeight);
    }

    /**
     * 处理下拉释放后是弹回还是刷新
     */
    private  void dealPullDownRelease() {
        if (mHeaderLayout.getLayoutParams().height >= mHeaderHeight - getTouchSlop()) {
            headerRefresh();
        } else {
            headerBack(mHeaderLayout.getLayoutParams().height);
        }
    }

    /**
     * 头部刷新
     */
    private void headerRefresh(){

        isRefreshing=true;

        mHeaderLayout.getLayoutParams().height = mHeaderHeight;
        mHeaderLayout.requestLayout();
            mChildView.setTranslationY(mHeaderHeight);
        mHeaderView.startAnim(mHeaderHeight,mHeaderHeight);
        if(mPullListener!=null){
            mPullListener.onRefresh();
        }
    }

    /**
     * 头部回到正常状态
     */
    private void headerBack(int startY){
        animLayoutByTime(startY,0,animHeadUpListener);
    }

    /**
     * 处理上拉释放后是弹回还是加载
     */
    private  void dealPullUpRelease() {
        if (mFooterLayout.getLayoutParams().height >= mFooterHeight - getTouchSlop()) {
            footerLoad();
        } else {
            footerBack(mFooterLayout.getLayoutParams().height);
        }
    }

    /**
     * 底部加载状态
     */
    private void footerLoad(){

        isLoading=true;

        mFooterLayout.getLayoutParams().height = mFooterHeight;
        mFooterLayout.requestLayout();
        mChildView.setTranslationY(-mFooterHeight);
        mFooterView.startAnim(mFooterHeight,mFooterHeight);
        if(mPullListener!=null){
            mPullListener.onLoadMore();
        }
    }

    /**
     * 底部回到正常状态
     */
    private void footerBack(int startY){
        mFooterLayout.getLayoutParams().height=0;
        mFooterLayout.requestLayout();
        mChildView.setTranslationY(0);
        mFooterView.onPullReleasing(0,mFooterHeight,mFooterHeight);

        animLayoutByTime(startY,0,animBottomUpListener);
    }


    /**
     * 根据起始点和结束点加载动画
     * @param start 起始点y轴数值
     * @param end   结束点y轴数值
     * @param listener 动画刷新监听器
     */
    private void animLayoutByTime(int start, int end, ValueAnimator.AnimatorUpdateListener listener) {
        ValueAnimator va = ValueAnimator.ofInt(start, end);
        va.setInterpolator(new DecelerateInterpolator());
        va.addUpdateListener(listener);
        va.setDuration((int) (Math.abs(start - end) * mAnimFraction));
        va.start();
    }


    /**
     *头部动画刷新监听器
     */
    private ValueAnimator.AnimatorUpdateListener animHeadUpListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            int height = (int) animation.getAnimatedValue();
            mHeaderLayout.getLayoutParams().height = height;
            mHeaderLayout.requestLayout();
            mChildView.setTranslationY(height);
            mHeaderView.onPullReleasing((float) height/mHeaderHeight,mHeaderHeight,mHeaderHeight);
        }
    };

    /**
     *底部动画刷新监听器
     */
    private ValueAnimator.AnimatorUpdateListener animBottomUpListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            int height = (int) animation.getAnimatedValue();
            mFooterLayout.getLayoutParams().height = height;
            mFooterLayout.requestLayout();
            mChildView.setTranslationY(-height);
            mFooterView.onPullReleasing(height,mFooterHeight,mFooterHeight);
            if(height==0){
                mFooterView.onFinish();
            }
        }
    };


    /**
     * =========================================================
     * 对外开放方法
     * =========================================================
     */

    /**
     * 停止刷新
     */
    public void stopRefresh(){

        isRefreshing=false;

        headerBack(mHeaderHeight);
    }


    /**
     * 停止加载更多
     */
    public void stopLoadMore(){

        isLoading=false;

        footerBack(mFooterHeight);
    }



    /**
     * 设置下拉刷新、上拉加载监听
     * @param pullListener  监听器
     */
    public void setPullListener(IPullListener pullListener){
        if(pullListener!=null){
            this.mPullListener=pullListener;
        }
    }


    /**
     * 设置底部view
     * @param mFooterView  底部view
     */
    public void setmFooterView(IBottomView mFooterView) {
        if(mFooterView!=null) {
            mFooterLayout.addView(mFooterView.getView());
            this.mFooterView = mFooterView;
        }
    }


    /**
     * 设置头部view
     * @param mHeaderView  头部view
     */
    public void setmHeaderView(IHeaderView mHeaderView) {
        if(mHeaderView!=null){
            mHeaderLayout.addView(mHeaderView.getView());
            this.mHeaderView = mHeaderView;
        }

    }

    /**
     * 获取touchslop值:系统判定滑动的最小粒度
     * @return 系统判定滑动的最小粒度
     */
    public int getTouchSlop(){
        return ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }
}
