package com.fxx.refreshlayout.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.fxx.refreshlayout.R;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.bt_list).setOnClickListener(this);
        findViewById(R.id.bt_recycler).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_list:
                startActivity(new Intent(MainActivity.this,ListViewActivity.class));
                break;
            case R.id.bt_recycler:
                startActivity(new Intent(MainActivity.this,RecyclerViewActivity.class));
                break;
        }
    }
}
