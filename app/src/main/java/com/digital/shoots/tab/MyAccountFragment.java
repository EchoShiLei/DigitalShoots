package com.digital.shoots.tab;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.digital.shoots.R;
import com.digital.shoots.db.greendao.UserDataManager;
import com.digital.shoots.db.greendao.bean.User;
import com.digital.shoots.utils.GlideEngine;
import com.digital.shoots.utils.ImageUtils;
import com.digital.shoots.views.UseInputDialog;
import com.luck.picture.lib.basic.PictureSelector;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnResultCallbackListener;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyAccountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyAccountFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private User mUser;
    private ImageView mIvUseIcon;
    private TextView mTvUseName;
    private TextView mTvTeamName;
    private TextView mTvBirthDate;

    public MyAccountFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyAccountFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyAccountFragment newInstance(String param1, String param2) {
        MyAccountFragment fragment = new MyAccountFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static MyAccountFragment newInstance() {
        MyAccountFragment fragment = new MyAccountFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    private void init(View view) {
        if (view == null) {
            return;
        }
        mUser = UserDataManager.getInstance().getUser();
        mIvUseIcon = view.findViewById(R.id.iv_user_icon);
        ImageUtils.createCircleImage(getActivity(), mIvUseIcon);
        mIvUseIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PictureSelector.create(getActivity())
                        .openGallery(SelectMimeType.ofImage())
                        .setMaxSelectNum(1)
                        .setImageEngine(GlideEngine.createGlideEngine())
                        .forResult(new OnResultCallbackListener<LocalMedia>() {
                            @Override
                            public void onResult(ArrayList<LocalMedia> result) {
                                if (result.size() > 0) {
                                    String availablePath = result.get(0).getAvailablePath();
                                    UserDataManager.getInstance().setUserIconPath(getActivity(), availablePath);
                                    ImageUtils.loadLocalPic(getActivity(), mIvUseIcon, availablePath);
                                }
                            }

                            @Override
                            public void onCancel() {

                            }
                        });
            }
        });
        mTvUseName = view.findViewById(R.id.tv_use_name);
        mTvUseName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(mTvUseName);
            }
        });
        mTvTeamName = view.findViewById(R.id.tv_team_name);

        View teamNameLayout = view.findViewById(R.id.ll_team_name_layout);
        teamNameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(mTvTeamName);
            }
        });
        View birthDateLayout = view.findViewById(R.id.ll_birth_date_layout);
        mTvBirthDate = view.findViewById(R.id.tv_birth_date);
        birthDateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });
        ImageView ivStarA = view.findViewById(R.id.tv_star_a);
        ImageView ivStarB = view.findViewById(R.id.tv_star_b);
        ImageView ivStarC = view.findViewById(R.id.tv_star_c);
        ImageView ivStarD = view.findViewById(R.id.tv_star_d);
        ImageView ivStarE = view.findViewById(R.id.tv_star_e);
        TextView tvHeightScores = view.findViewById(R.id.btn_height_scores);
        TextView tvDataDate = view.findViewById(R.id.tv_data_date);
        TextView tvDataScore = view.findViewById(R.id.tv_data_score);
        TextView tvDataSpeed = view.findViewById(R.id.tv_data_speed);
        ImageView tvDataVideo = view.findViewById(R.id.iv_data_video);
        fullInfo();
    }


    private void showDialog(TextView textView) {
        Activity activity = getActivity();
        if (textView == null) {
            return;
        }
        UseInputDialog dialog = new UseInputDialog(activity, R.style.MyDialog);
        dialog.setTintText(textView.getText().toString());
        dialog.setDialogClickListener(new UseInputDialog.DialogClickListener() {
            @Override
            public void onOkOnclick(String content) {
                textView.setText(content);
                int id = textView.getId();
                if (id == R.id.tv_use_name) {
                    UserDataManager.getInstance().setUserName(activity, content);
                }
                if (id == R.id.tv_team_name) {
                    UserDataManager.getInstance().setUserTeamName(activity, content);
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void showDatePickerDialog() {
        Calendar data = Calendar.getInstance();
        DatePickerDialog dpd = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                String date = String.valueOf(year) + "." + String.valueOf(month) + "." + String.valueOf(day);
                mTvBirthDate.setText(date);
                UserDataManager.getInstance().setUserBirthdate(getActivity(), date);
            }

        }, data.get(Calendar.YEAR), data.get(Calendar.MONTH), data.get(Calendar.DAY_OF_MONTH));
        dpd.show();

    }


    private void fullInfo() {
        if (!TextUtils.isEmpty(mUser.iconPath)) {
            ImageUtils.loadLocalPic(getActivity(), mIvUseIcon, mUser.iconPath);
        }
        if (!TextUtils.isEmpty(mUser.name)) {
            mTvUseName.setText(mUser.name);
        }
        if (!TextUtils.isEmpty(mUser.teamName)) {
            mTvTeamName.setText(mUser.teamName);
        }
        if (!TextUtils.isEmpty(mUser.birthdate)) {
            mTvBirthDate.setText(mUser.birthdate);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}