package com.wintone.site.ui.fragment;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.wintone.site.R;
import com.wintone.site.networkmodel.PersonSignalModel;
import com.wintone.site.ui.base.fragment.BaseFragment;
import com.wintone.site.widget.CircleImageView;

import butterknife.BindView;


public class PersonInfoFragment extends BaseFragment {

    @BindView(R.id.face_header_image) CircleImageView face_header_image;
    @BindView(R.id.nameTextView)      TextView nameTextView;
    @BindView(R.id.sexTextView)       TextView sexTextView;
    @BindView(R.id.nationTextView)    TextView nationTextView;
    @BindView(R.id.idNoTextView)      TextView idNoTextView;
    @BindView(R.id.birthdayTextView)  TextView birthdayTextView;
    @BindView(R.id.addressTextView)   TextView addressTextView;
    @BindView(R.id.idIssuerTextView)  TextView idIssuerTextView;
    @BindView(R.id.idDateTextView)    TextView idDateTextView;
    @BindView(R.id.bankNoTextView)    TextView bankNoTextView;
    @BindView(R.id.bankNameTextView)  TextView bankNameTextView;

    @BindView(R.id.idFrontImageView)  ImageView idFrontImageView;
    @BindView(R.id.idBackImageView)   ImageView idBackImageView;
    @BindView(R.id.bankImageView)     ImageView bankImageView;

    private static PersonSignalModel mPersonSignalModel;

    private PersonInfoFragment() {
    }

    public static PersonInfoFragment newInstance(PersonSignalModel personSignalModel) {
        PersonInfoFragment fragment = new PersonInfoFragment();
        mPersonSignalModel = personSignalModel;
        return fragment;
    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_person_info;
    }

    @Override
    protected void initView(View view) {
        Glide.with(getActivity()).load(mPersonSignalModel.getResult().getFaceUrl()).into(face_header_image);

        nameTextView.setText(mPersonSignalModel.getResult().getEmpName());
        idNoTextView.setText(mPersonSignalModel.getResult().getIdCode());
//        birthdayTextView.setText(mPersonSignalModel);

//        if(null != mPersonSignalModel.getResult().get)
        Glide.with(getActivity()).load(mPersonSignalModel.getResult().getIdphotoScan()).into(idFrontImageView);

        Glide.with(getActivity()).load(mPersonSignalModel.getResult().getIdphotoScan2()).into(idBackImageView);
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
