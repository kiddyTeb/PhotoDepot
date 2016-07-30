package com.liangdekai.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.liangdekai.photodepot.R;
import com.liangdekai.util.LoadImage;

import java.util.List;

public class PhotoAdapter extends BaseAdapter{
    private List<String> mImageList ;
    private Context mContext ;
    private LoadImage mLoadImage ;

    public PhotoAdapter(Context context , List<String> list){
        this.mContext = context ;
        this.mImageList = list ;
        mLoadImage = LoadImage.getInstance();
    }


    @Override
    public int getCount() {
        return mImageList.size();
    }

    @Override
    public Object getItem(int i) {
        return mImageList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder ;
        View view ;
        if (convertView == null){
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.gridview_item , viewGroup , false);
            viewHolder.imageView = (ImageView) view.findViewById(R.id.main_iv_item);
            view.setTag(viewHolder);
        }else {
            view = convertView ;
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.imageView.setImageResource(R.mipmap.empty);
        viewHolder.imageView.setTag(mImageList.get(i));
        mLoadImage.loadImage(mImageList.get(i) , viewHolder.imageView);
        return view;
    }

    class ViewHolder {
        ImageView imageView ;
    }
}
