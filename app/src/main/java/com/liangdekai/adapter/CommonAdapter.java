package com.liangdekai.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;

import java.util.List;

public abstract class CommonAdapter<T> extends BaseAdapter{
    protected LayoutInflater mInflater ;
    protected Context mContext ;
    protected List<T> mData;

    public CommonAdapter(Context context , List<T> mData){
        mInflater = LayoutInflater.from(context);
        this.mContext = context ;
        this.mData = mData ;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int i) {
        return mData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

}
