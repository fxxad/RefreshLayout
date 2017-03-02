package com.fxx.refreshlayout.activity;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.fxx.refreshlayout.R;
import com.fxx.refreshlayout.adapter.RvAdapter;
import com.fxx.refreshlayout.view.IPullListener;
import com.fxx.refreshlayout.view.RefreshLayout;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewActivity extends AppCompatActivity implements IPullListener,
        RvAdapter.OnItemClickListener{

    private RecyclerView mRv;
    private RefreshLayout mRefreshLayout;

    private List<String> mStrs;
    private RvAdapter mRvAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);
        initView();
        initData();
    }

    /**
     * 初始化view
     */
    private void initView(){
        mRefreshLayout= (RefreshLayout) findViewById(R.id.refresh_recycler);
        mRefreshLayout.setPullListener(this);
        mRv= (RecyclerView) findViewById(R.id.rv);
        mRv.setLayoutManager(new LinearLayoutManager(RecyclerViewActivity.this));
        mRv.addItemDecoration(new DividerItemDecoration(RecyclerViewActivity.this,
                DividerItemDecoration.VERTICAL));
        mRv.setItemAnimator(new DefaultItemAnimator());
    }

    private void initData(){
        mStrs=new ArrayList<>();
        for(int i=0;i<30;i++){
            mStrs.add("item "+i);
        }
        mRvAdapter=new RvAdapter(RecyclerViewActivity.this.getBaseContext(),mStrs);
        mRvAdapter.setmOnItemClickListener(this);
        mRv.setAdapter(mRvAdapter);
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mRefreshLayout.stopRefresh();
            }
        }, 3000);
    }

    @Override
    public void onLoadMore() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mStrs.add("item new");
                mRvAdapter.notifyDataSetChanged();
                mRefreshLayout.stopLoadMore();
            }
        },3000);

    }

    @Override
    public void onItemCLick(View view, int position) {
        Toast.makeText(RecyclerViewActivity.this,mStrs.get(position)+" click",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemLongClick(View view, int position) {
        Toast.makeText(RecyclerViewActivity.this,mStrs.get(position)+" long click",Toast.LENGTH_SHORT).show();

    }
}
