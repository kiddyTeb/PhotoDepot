package com.liangdekai.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

public abstract class CommonAdapter<T> extends BaseAdapter{
    protected LayoutInflater mInflater ;
    protected Context mContext ;
    protected List<T> mDatas ;

    public CommonAdapter(Context context , List<T> mDatas){
        mInflater = LayoutInflater.from(context);
        this.mContext = context ;
        this.mDatas = mDatas ;
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int i) {
        return mDatas.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

}
