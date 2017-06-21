package com.amtee.camscanner.custom_adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amtee.camscanner.R;
import com.amtee.camscanner.utilities.cache.ImageLoader;
import com.amtee.camscanner.model_classes.MyDocItems;

import java.util.ArrayList;


/**
 * Created by DEVEN SINGH on 2/2/2015.
 */

public class DocCreationListAdapter extends BaseAdapter {

    private boolean mBusy = false;

    public void setFlagBusy(boolean busy) {
        this.mBusy = busy;
    }


    private ImageLoader mImageLoader;
    private Context mContext;
    private ArrayList<MyDocItems> data;


    public DocCreationListAdapter( Context context, ArrayList<MyDocItems> data) {
        this.mContext = context;
        mImageLoader = new ImageLoader(context);
        this.data=data;
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.doc_creation_list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.mTextView = (TextView) convertView
                    .findViewById(R.id.doc_item_num);
            viewHolder.mImageView = (ImageView) convertView
                    .findViewById(R.id.doc_img);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String url = "";
          url=data.get(position % data.size()).getMyDocImgPathName();
        String docN="";
        docN=data.get(position % data.size()).getMyDocName();
        viewHolder.mImageView.setImageResource(R.mipmap.image_waiting);


        if (!mBusy) {
            mImageLoader.DisplayImage(url, viewHolder.mImageView, false);
            viewHolder.mTextView.setText(" " + (position + 1));
        } else {
            mImageLoader.DisplayImage(url, viewHolder.mImageView, false);
            viewHolder.mTextView.setText("processing.......");
        }
        return convertView;
    }

    static class ViewHolder {
        TextView mTextView;
        ImageView mImageView;
    }
}
