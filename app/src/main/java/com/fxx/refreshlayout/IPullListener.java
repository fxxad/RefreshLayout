package com.fxx.refreshlayout;

/**
 * Created by mc on 2017/2/28.
 */

/**
 * 下拉刷新、上拉加载更多监听接口
 */
public interface IPullListener {
    /**
     * 刷新
     */
    void onRefresh();

    /**
     * 加载更多
     */
    void onLoadMore();
}
