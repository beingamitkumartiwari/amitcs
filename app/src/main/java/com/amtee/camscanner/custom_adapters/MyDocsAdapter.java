package com.amtee.camscanner.custom_adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.amtee.camscanner.R;
import com.amtee.camscanner.utilities.cache.ImageLoader;
import com.amtee.camscanner.model_classes.MyDocItems;

import java.util.ArrayList;


/**
 * Created by DEVEN SINGH on 2/9/2015.
 */
public class MyDocsAdapter extends BaseAdapter {

    private boolean mBusy = false;
    private boolean isActionMultiplePick;

    public void setFlagBusy(boolean busy) {
        this.mBusy = busy;
    }


    private ImageLoader mImageLoader;
    private Context mContext;
    private ArrayList<MyDocItems> data;


    public MyDocsAdapter(Context context, ArrayList<MyDocItems> data) {
        this.mContext = context;
        mImageLoader = new ImageLoader(context);
        this.data = data;

    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }

    public void setMultiplePick(boolean isMultiplePick) {
        this.isActionMultiplePick = isMultiplePick;
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
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        System.out.println("getView called  ");
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.my_doc_list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.myDocName = (TextView) convertView
                    .findViewById(R.id.myDocName);
            viewHolder.mImageView = (ImageView) convertView
                    .findViewById(R.id.imageThumbItem);
            viewHolder.mDateTime = (TextView) convertView
                    .findViewById(R.id.dateTime);
            viewHolder.mDocPages = (TextView) convertView
                    .findViewById(R.id.totalDocPages);
            viewHolder.mTags = (TextView) convertView
                    .findViewById(R.id.tags);
            viewHolder.checkMerge = (CheckBox) convertView.findViewById(R.id.checkMerge);

//            if (isActionMultiplePick) {
//                viewHolder.checkMerge.setVisibility(View.VISIBLE);
//            } else {
//                viewHolder.checkMerge.setVisibility(View.GONE);
//            }

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            if (isActionMultiplePick) {
                viewHolder.checkMerge.setVisibility(View.VISIBLE);
            } else {
                viewHolder.checkMerge.setVisibility(View.GONE);
            }
        }


        //      viewHolder.mImageView.setTag(position);
//        if (isActionMultiplePick) {
//            viewHolder.checkMerge
//                    .setSelected(data.get(position % data.size()).isSeleted());
//        }
        if (data.get(position).isSeleted()) {
            viewHolder.checkMerge.setChecked(true);
        } else {
            viewHolder.checkMerge.setChecked(false);
        }
        String url = "";
        url = data.get(position % data.size()).getMyDocImgPathName();
        String docName = "";
        docName = data.get(position % data.size()).getMyDocName();
        String docTime = "";
        docTime = data.get(position % data.size()).getMyDocTime();
        String docTag = "";
        docTag = data.get(position % data.size()).getMyDocTags();

        viewHolder.mImageView.setImageResource(R.mipmap.image_waiting);


        if (!mBusy) {
            mImageLoader.DisplayImage(url, viewHolder.mImageView, false);
            viewHolder.myDocName.setText("" + docName);
            viewHolder.mDateTime.setText(""+docTime);
            viewHolder.mDocPages.setText("");
            viewHolder.mTags.setText(""+docTag);

        } else {
            mImageLoader.DisplayImage(url, viewHolder.mImageView, false);
            viewHolder.myDocName.setText("processing.......");
            viewHolder.mDateTime.setText("");
            viewHolder.mDocPages.setText("");
            viewHolder.mTags.setText("");
        }
        return convertView;
    }

    static class ViewHolder {
        TextView myDocName;
        ImageView mImageView;
        TextView mDateTime;
        TextView mDocPages;
        TextView mTags;
        CheckBox checkMerge;
    }


    public boolean isAnySelected() {
        boolean isAnySelected = false;
         for (int i = 0; i < data.size(); i++) {
            if (data.get(i).isSeleted()) {
                isAnySelected = true;
                break;
            }
        }
         return isAnySelected;
    }

    public boolean isAtleastTwoSelected() {
        boolean isAtleastTwoSelected = false;
        int count = 0;

        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).isSeleted()) {
                count++;
                if (count >= 2) {
                    isAtleastTwoSelected = true;
                    break;
                }
            }
        }

        return isAtleastTwoSelected;
    }

    public ArrayList<MyDocItems> getSelected() {
        ArrayList<MyDocItems> dataT = new ArrayList<MyDocItems>();

        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).isSeleted()) {
                dataT.add(data.get(i));
            }
        }

        return dataT;
    }

    public void changeSelection(int position) {
 //        data.get(position).setSeleted(true);
         if (data.get(position).isSeleted()) {
             data.get(position).setSeleted(false);
        } else {
            data.get(position).setSeleted(true);
        }
//
//        ((ViewHolder) v.getTag()).checkMerge.setSelected(data
//                .get(position % data.size()).isSeleted());
        notifyDataSetChanged();
    }

    public String getDocsName(int position) {
        return data.get(position).getMyDocName();
    }
}
