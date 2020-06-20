package com.wintone.site.ui.fragment;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.kyleduo.switchbutton.SwitchButton;
import com.wintone.site.R;
import com.wintone.site.networkmodel.PersonSignalModel;
import com.wintone.site.ui.base.fragment.BaseFragment;

import butterknife.BindView;


public class ProjectInfoFragment extends BaseFragment {

    @BindView(R.id.projectTextView)     TextView projectTextView;
    @BindView(R.id.companyTextView)     TextView companyTextView;
    @BindView(R.id.teamTextView)        TextView teamTextView;
    @BindView(R.id.leaderSwitch)        SwitchButton leaderSwitch;
    @BindView(R.id.workTypeTextView)    TextView workTypeTextView;
    @BindView(R.id.entranceDateTextView)TextView entranceDateTextView;
    @BindView(R.id.phoneTextView)       TextView phoneTextView;

    private static PersonSignalModel mPersonSignalModel;

    private ProjectInfoFragment() {
    }

    public static ProjectInfoFragment newInstance(PersonSignalModel personSignalModel) {
        ProjectInfoFragment fragment = new ProjectInfoFragment();
        mPersonSignalModel = personSignalModel;
        return fragment;
    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_project_info;
    }

    @Override
    protected void initView(View view) {
        projectTextView.setText(mPersonSignalModel.getResult().getProjectName());
        if(null != mPersonSignalModel.getResult().getConstructionName() && !TextUtils.isEmpty(mPersonSignalModel.getResult().getConstructionName())){
            companyTextView.setText(mPersonSignalModel.getResult().getConstructionName());
        }

        if(null != mPersonSignalModel.getResult().getTeamName() && !TextUtils.isEmpty(mPersonSignalModel.getResult().getConstructionName())){
            teamTextView.setText(mPersonSignalModel.getResult().getTeamName());
        }

        if(mPersonSignalModel.getResult().getIsTeam() == 0){
            leaderSwitch.setChecked(false);
        }else{
            leaderSwitch.setChecked(true);
        }

        workTypeTextView.setText(mPersonSignalModel.getResult().getJobName());
//        TimeUtils.millis2String(mPersonSignalModel.getResult().getCreateDate())
        entranceDateTextView.setText(mPersonSignalModel.getResult().getStartTime());

//        phoneTextView.setText(mPersonSignalModel.getResult().getStartTime());
    }

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }

    @Override
    public void showError(String msg) {

    }

    @Override
    public void loadData() {

    }
}
