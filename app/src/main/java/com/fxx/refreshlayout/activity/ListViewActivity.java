package com.fxx.refreshlayout.activity;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.fxx.refreshlayout.view.IPullListener;
import com.fxx.refreshlayout.R;
import com.fxx.refreshlayout.view.RefreshLayout;

import java.util.ArrayList;
import java.util.List;


public class ListViewActivity extends AppCompatActivity implements IPullListener, AdapterView.OnItemClickListener{
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
        setContentView(R.layout.activity_list_view);
        initView();
        initData();
    }

    private void initView(){
        mLvMy= (ListView) findViewById(R.id.lv_my);
        mLvMy.setOnItemClickListener(this);
        mRefreshLayout = (RefreshLayout) findViewById(R.id.my_refresh);
        mRefreshLayout.setPullListener(this);
    }

    private void initData(){
        mStrs=new ArrayList<>();
        for(int i=0;i<20;i++){
            mStrs.add("item "+i);
        }
        mAdapter=new ArrayAdapter<>(ListViewActivity.this,
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        finish();
    }
}
