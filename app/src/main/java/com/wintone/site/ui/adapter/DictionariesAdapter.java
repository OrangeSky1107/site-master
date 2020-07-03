package com.wintone.site.ui.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wintone.site.R;
import com.wintone.site.networkmodel.DictionariesModel;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

/**
 * create by ths on 2020/7/2
 */
public class DictionariesAdapter extends RecyclerView.Adapter<DictionariesAdapter.DictionariesViewHolder>{

    private AsyncListDiffer<DictionariesModel.ResultBean.RecordsBean> mAsyncListDiffer;
    private DictionariesAdapter.OnItemClickListener mOnItemClickListener;

    private DiffUtil.ItemCallback<DictionariesModel.ResultBean.RecordsBean> mModelItemCallback = new DiffUtil.ItemCallback<DictionariesModel.ResultBean.RecordsBean>() {
        @Override
        public boolean areItemsTheSame(@NonNull DictionariesModel.ResultBean.RecordsBean oldItem, @NonNull DictionariesModel.ResultBean.RecordsBean newItem) {
            return TextUtils.equals(oldItem.getId(),newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull DictionariesModel.ResultBean.RecordsBean oldItem, @NonNull DictionariesModel.ResultBean.RecordsBean newItem) {
            return oldItem.getTitle().equals(newItem.getTitle());
        }
    };

    public DictionariesAdapter(){
        mAsyncListDiffer = new AsyncListDiffer<DictionariesModel.ResultBean.RecordsBean>(this,mModelItemCallback);
    }

    @NonNull
    @Override
    public DictionariesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.dictionaries_item_layout,parent,false);
        return new DictionariesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DictionariesViewHolder holder, int position) {
        holder.setData(position,getItem(position));
    }

    @Override
    public int getItemCount() {
        return mAsyncListDiffer.getCurrentList().size();
    }

    public DictionariesModel.ResultBean.RecordsBean getItem(int position){
        return mAsyncListDiffer.getCurrentList().get(position);
    }

    public void submitList(List<DictionariesModel.ResultBean.RecordsBean> listModels){
        mAsyncListDiffer.submitList(listModels);
    }

    class DictionariesViewHolder extends RecyclerView.ViewHolder{

        private TextView dictionariesItem;

        public DictionariesViewHolder(@NonNull View itemView) {
            super(itemView);
            dictionariesItem = (TextView)itemView.findViewById(R.id.dictionariesItem);
        }

        public void setData(int position,DictionariesModel.ResultBean.RecordsBean recordsBean){
            dictionariesItem.setText(recordsBean.getTitle());

            dictionariesItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onClickItem(position,recordsBean);
                }
            });
        }
    }

    public interface OnItemClickListener{
        void onClickItem(int position,DictionariesModel.ResultBean.RecordsBean recordsBean);
    }

    public void setOnItemClickListener(DictionariesAdapter.OnItemClickListener onItemClickListener){
        this.mOnItemClickListener = onItemClickListener;
    }
}
