package com.fxx.refreshlayout.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fxx.refreshlayout.R;

import java.util.List;

/**
 * Created by mc on 2017/3/2.
 * recyclerview适配器，封装了itemView的click和longclick事件回调
 */

public class RvAdapter extends RecyclerView.Adapter<RvAdapter.ViewHolder>{

    private List<String> mStrs;
    private Context mContext;
    //click和longclick回调接口
    private OnItemClickListener mOnItemClickListener;

    public RvAdapter(Context context,List<String> strs){
        mContext=context;
        mStrs=strs;
    }

    @Override
    public RvAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.view_rv_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.tvTitle.setText(mStrs.get(position));
        if(mOnItemClickListener!=null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos=holder.getLayoutPosition();
                    mOnItemClickListener.onItemCLick(holder.itemView,pos);
                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int pos=holder.getLayoutPosition();
                    mOnItemClickListener.onItemLongClick(holder.itemView,pos);
                    return true;
                }
            });
        }
    }


    @Override
    public int getItemCount() {
        return mStrs.size();
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        mContext=null;
        super.onDetachedFromRecyclerView(recyclerView);
    }

    /**
     * 设置click和longclick回调接口
     * @param mOnItemClickListener
     */
    public void setmOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvTitle;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTitle= (TextView) itemView.findViewById(R.id.tv_title);
        }
    }

    /**
     * click和longclick事件接口
     */
    public interface OnItemClickListener{
        void onItemCLick(View view,int position);
        void onItemLongClick(View view,int position);
    }
}
