package com.wintone.site.ui.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wintone.site.R;
import com.wintone.site.networkmodel.ProjectListModel;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

/**
 * create by ths on 2020/6/23
 */
public class ProjectListAdapter extends RecyclerView.Adapter<ProjectListAdapter.ProjectViewHolder> {

    private AsyncListDiffer<ProjectListModel> mAsyncListDiffer;

    private DiffUtil.ItemCallback<ProjectListModel> mModelItemCallback = new DiffUtil.ItemCallback<ProjectListModel>() {
        @Override
        public boolean areItemsTheSame(@NonNull ProjectListModel oldItem, @NonNull ProjectListModel newItem) {
            return TextUtils.equals(oldItem.getId(),newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull ProjectListModel oldItem, @NonNull ProjectListModel newItem) {
            return oldItem.getProjectName().equals(newItem.getProjectName());
        }
    };

    public ProjectListAdapter(){
        mAsyncListDiffer = new AsyncListDiffer<ProjectListModel>(this,mModelItemCallback);
    }

    @NonNull
    @Override
    public ProjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.project_item_layout,parent,false);
        return new ProjectViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectViewHolder holder, int position) {
        holder.setData(getItem(position));
    }

    @Override
    public int getItemCount() {
        return mAsyncListDiffer.getCurrentList().size();
    }

    public void submitList(List<ProjectListModel> listModels){
        mAsyncListDiffer.submitList(listModels);
    }

    public ProjectListModel getItem(int position){
        return mAsyncListDiffer.getCurrentList().get(position);
    }

    class ProjectViewHolder extends RecyclerView.ViewHolder{

        private TextView projectTitle;
        private TextView addressName;
        private TextView companyName;

        public ProjectViewHolder(View view){
            super(view);
            projectTitle = view.findViewById(R.id.projectTitle);
            addressName = view.findViewById(R.id.addressName);
            companyName = view.findViewById(R.id.companyName);
        }

        public void setData(ProjectListModel listModel){
            projectTitle.setText(listModel.getProjectName());
            addressName.setText(listModel.getProjectAddress());
            companyName.setText(listModel.getCompanyName());
        }
    }
}
