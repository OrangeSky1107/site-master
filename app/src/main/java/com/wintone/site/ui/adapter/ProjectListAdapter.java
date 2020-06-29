package com.wintone.site.ui.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wintone.site.R;
import com.wintone.site.networkmodel.ProjectModel;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

/**
 * create by ths on 2020/6/23
 */
public class ProjectListAdapter extends RecyclerView.Adapter<ProjectListAdapter.ProjectViewHolder> {

    private AsyncListDiffer<ProjectModel.ResultBean.RecordsBean> mAsyncListDiffer;
    private OnItemClickListener mOnItemClickListener;

    private DiffUtil.ItemCallback<ProjectModel.ResultBean.RecordsBean> mModelItemCallback = new DiffUtil.ItemCallback<ProjectModel.ResultBean.RecordsBean>() {
        @Override
        public boolean areItemsTheSame(@NonNull ProjectModel.ResultBean.RecordsBean oldItem, @NonNull ProjectModel.ResultBean.RecordsBean newItem) {
            return TextUtils.equals(oldItem.getId(),newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull ProjectModel.ResultBean.RecordsBean oldItem, @NonNull ProjectModel.ResultBean.RecordsBean newItem) {
            return oldItem.getProjectName().equals(newItem.getProjectName());
        }
    };

    public ProjectListAdapter(){
        mAsyncListDiffer = new AsyncListDiffer<ProjectModel.ResultBean.RecordsBean>(this,mModelItemCallback);
    }

    @NonNull
    @Override
    public ProjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.project_item_layout,parent,false);
        return new ProjectViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectViewHolder holder, int position) {
        holder.setData(position,getItem(position));
    }

    @Override
    public int getItemCount() {
        return mAsyncListDiffer.getCurrentList().size();
    }

    public void submitList(List<ProjectModel.ResultBean.RecordsBean> listModels){
        mAsyncListDiffer.submitList(listModels);
    }

    public ProjectModel.ResultBean.RecordsBean getItem(int position){
        return mAsyncListDiffer.getCurrentList().get(position);
    }

    class ProjectViewHolder extends RecyclerView.ViewHolder{

        private TextView projectTitle;
        private TextView addressName;
        private TextView companyName;
        private ConstraintLayout mConstraintLayout;

        public ProjectViewHolder(View view){
            super(view);
            projectTitle = view.findViewById(R.id.projectTitle);
            addressName = view.findViewById(R.id.addressName);
            companyName = view.findViewById(R.id.companyName);
            mConstraintLayout = view.findViewById(R.id.constraintLayout);
        }

        public void setData(int position,ProjectModel.ResultBean.RecordsBean listModel){
            projectTitle.setText(listModel.getProjectName());
            addressName.setText(listModel.getProjectAddress());
            companyName.setText(listModel.getCompanyName());

            mConstraintLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onClickItem(position,listModel);
                }
            });
        }
    }

    public interface OnItemClickListener{
        void onClickItem(int position,ProjectModel.ResultBean.RecordsBean recordsBean);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.mOnItemClickListener = onItemClickListener;
    }
}
