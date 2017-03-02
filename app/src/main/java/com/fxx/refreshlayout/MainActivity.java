package com.fxx.refreshlayout;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements IPullListener{
    /**
     * view
     */
    private ListView mLvMy;
    private RefreshLayout mRefreshLayout;

    /**
     * data
     */
    private List<String> mStrs;
    private ArrayAdapter<String> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }

    private void initView(){
        mLvMy= (ListView) findViewById(R.id.lv_my);
        mRefreshLayout = (RefreshLayout) findViewById(R.id.my_refresh);
        mRefreshLayout.setPullListener(this);
    }

    private void initData(){
        mStrs=new ArrayList<>();
        for(int i=0;i<20;i++){
            mStrs.add("item "+i);
        }
        mAdapter=new ArrayAdapter<>(MainActivity.this,
                android.R.layout.simple_list_item_1, mStrs);
        mLvMy.setAdapter(mAdapter);
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mRefreshLayout.stopRefresh();
            }
        },3000);
    }

    @Override
    public void onLoadMore() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mStrs.add("item new");
                mAdapter.notifyDataSetChanged();
                mRefreshLayout.stopLoadMore();
            }
        },3000);
    }
}
