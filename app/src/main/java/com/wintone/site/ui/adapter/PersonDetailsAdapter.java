package com.wintone.site.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.wintone.site.R;
import com.wintone.site.networkmodel.PersonDetailsModel;

import java.util.List;

/**
 * create by ths on 2020/6/19
 */
public class PersonDetailsAdapter extends BaseAdapter {

    private List<PersonDetailsModel.ResultBean.RecordsBean> mRecordsBeanList;
    private Context mContext;
    private LayoutInflater mInflater;

    public PersonDetailsAdapter(Context context, List<PersonDetailsModel.ResultBean.RecordsBean> list){
        this.mRecordsBeanList = list;
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mRecordsBeanList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_person_info, null);
            holder = new ViewHolder();
            holder.headerImage = (ImageView) convertView.findViewById(R.id.headerImage);
            holder.nameText = (TextView)convertView.findViewById(R.id.nameText);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Glide.with(mContext).load(mRecordsBeanList.get(position).getFaceUrl()).into(holder.headerImage);
        holder.nameText.setText(mRecordsBeanList.get(position).getEmpName());
        return convertView;
    }

    final class ViewHolder{

        public ImageView headerImage;
        public TextView nameText;

    }
}
