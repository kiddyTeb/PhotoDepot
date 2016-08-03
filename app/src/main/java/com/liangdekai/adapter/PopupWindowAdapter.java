package com.liangdekai.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.liangdekai.bean.ImageFolder;
import com.liangdekai.photodepot.R;
import com.liangdekai.util.CompressImage;

import java.util.List;

public class PopupWindowAdapter extends BaseAdapter {
    private Context mContext ;
    private List<ImageFolder> mFolder ;

    public PopupWindowAdapter(Context context, List<ImageFolder> folder) {
        this.mContext = context ;
        this.mFolder = folder ;
    }

    @Override
    public int getCount() {
        return mFolder.size();
    }

    @Override
    public Object getItem(int i) {
        return mFolder.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder holder ;
        View view ;
        if (convertView == null){
            view = LayoutInflater.from(mContext).inflate(R.layout.part_lv_item , null);
            holder = new ViewHolder();
            holder.imageView = (ImageView) view.findViewById(R.id.part_item_image);
            holder.FileName = (TextView) view.findViewById(R.id.part_item_name);
            holder.ImageCount = (TextView) view.findViewById(R.id.part_item_count);
            view.setTag(holder);
        }else{
            view = convertView ;
            holder = (ViewHolder) view.getTag();
        }
        holder.imageView.setImageBitmap(CompressImage.compressImage(mFolder.get(i).getFirstImagePath() , 100 , 100));
        holder.FileName.setText(mFolder.get(i).getFolderName());
        holder.ImageCount.setText(mFolder.get(i).getFileCount());
        return view;
    }

    class ViewHolder{
        ImageView imageView ;
        TextView FileName ;
        TextView ImageCount ;
    }
}
