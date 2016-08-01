package com.liangdekai.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.liangdekai.activity.ImageDetailActivity;
import com.liangdekai.photodepot.R;
import com.liangdekai.util.LoadImage;

import java.util.ArrayList;
import java.util.List;

public class ChooseImageAdapter extends BaseAdapter{
    private List<String> mSelectedImage;
    private List<String> mImageList ;
    private Context mContext ;
    private LoadImage mLoadImage ;

    public ChooseImageAdapter(Context context , List<String> list){
        this.mContext = context ;
        this.mImageList = list ;
        mLoadImage = LoadImage.getInstance();
        mSelectedImage = new ArrayList<String>();
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
    public View getView(final int i, View convertView, ViewGroup viewGroup) {
        final ViewHolder viewHolder ;
        View view ;
        if (convertView == null){
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.item_gv_choose_image, viewGroup , false);
            viewHolder.imageView = (ImageView) view.findViewById(R.id.choose_image_iv_item);
            viewHolder.imageButton = (ImageButton) view.findViewById(R.id.main_ib_select);
            view.setTag(viewHolder);
        }else {
            view = convertView ;
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.imageView.setImageResource(R.mipmap.empty);
        viewHolder.imageView.setTag(mImageList.get(i));
        //viewHolder.imageButton.setImageResource(R.mipmap.picture_unselected);

        viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageDetailActivity.startActivity(mContext , mImageList , i);
            }
        });
        viewHolder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onButtonClick(mImageList.get(i) , viewHolder);
            }
        });

        mLoadImage.loadImage(mImageList.get(i) , viewHolder.imageView);
        return view;
    }

    private void onButtonClick(String path , ViewHolder viewHolder){
        if (mSelectedImage.contains(path)){
            mSelectedImage.remove(path);
            viewHolder.imageButton.setImageResource(R.mipmap.picture_unselected);
            viewHolder.imageView.setColorFilter(null);
        }else{
            mSelectedImage.add(path);
            viewHolder.imageButton.setImageResource(R.mipmap.pictures_selected);
            viewHolder.imageView.setColorFilter(Color.parseColor("#77000000"));
        }
    }

    public List<String> getSelectedList(){
        return mSelectedImage;
    }

    class ViewHolder {
        ImageView imageView ;
        ImageButton imageButton ;
    }
}
