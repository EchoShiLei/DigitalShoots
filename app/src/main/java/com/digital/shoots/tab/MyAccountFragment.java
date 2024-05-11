package com.digital.shoots.tab;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Outline;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.digital.shoots.R;
import com.digital.shoots.db.greendao.GreenDaoManager;
import com.digital.shoots.db.greendao.UserDataManager;
import com.digital.shoots.db.greendao.bean.GameAchievement;
import com.digital.shoots.db.greendao.bean.User;
import com.digital.shoots.events.UserInfoRefreshManger;
import com.digital.shoots.utils.GlideEngine;
import com.digital.shoots.utils.ImageUtils;
import com.digital.shoots.utils.ToastUtils;
import com.digital.shoots.utils.Utils;
import com.digital.shoots.views.ImageFileCropEngine;
import com.digital.shoots.views.MeOnMediaEditInterceptListener;
import com.digital.shoots.views.UseInputDialog;
import com.luck.picture.lib.basic.PictureSelector;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.engine.CropEngine;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnResultCallbackListener;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;
import com.yalantis.ucrop.UCrop;

import java.io.File;
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
    private TextView mTvUseNameTop;
    private TextView mTvTeamName;
    private TextView mTvBirthDate;
    private TextView mTvUseName;
    private ImageView mIvDataVideo;
    private GameAchievement mJuniorHighestScore;
    private TextView mTvDataDate;
    private TextView mTvDataScore;
    private TextView mTvDataSpeed;
    private ImageView mIvStarA;
    private ImageView mIvStarB;
    private ImageView mIvStarC;
    private ImageView mIvStarD;
    private ImageView mIvStarE;

    private StandardGSYVideoPlayer videoPlayer;

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
        mJuniorHighestScore = GreenDaoManager.getJuniorHighestScore();
        Log.i("zyw", "mJuniorHighestScore = " + mJuniorHighestScore);
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
                        .setCropEngine(new ImageFileCropEngine(getSandboxPath(), buildOptions()))
                        .setEditMediaInterceptListener(new MeOnMediaEditInterceptListener(getSandboxPath(), buildOptions()))
                        .forResult(new OnResultCallbackListener<LocalMedia>() {
                            @Override
                            public void onResult(ArrayList<LocalMedia> result) {
                                if (result.size() > 0) {
                                    String availablePath = result.get(0).getAvailablePath();
                                    UserDataManager.getInstance().setUserIconPath(getActivity(), availablePath);
                                    ImageUtils.loadLocalPic(getActivity(), mIvUseIcon, availablePath);
                                    UserInfoRefreshManger.getInstance().notifyUserInfoRefresh();
                                }
                            }

                            @Override
                            public void onCancel() {

                            }
                        });
            }


        });
        mTvUseNameTop = view.findViewById(R.id.tv_use_name_top);
//        mTvUseNameTop.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showDialog(mTvUseNameTop);
//            }
//        });

        View teamNameLayout = view.findViewById(R.id.ll_team_name_layout);
        mTvTeamName = view.findViewById(R.id.tv_team_name);
        teamNameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(mTvTeamName);
            }
        });


        View useNameLayout = view.findViewById(R.id.ll_user_name_layout);
        mTvUseName = view.findViewById(R.id.tv_use_name);
        useNameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(mTvUseName);
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
        mIvStarA = view.findViewById(R.id.tv_star_a);
        mIvStarB = view.findViewById(R.id.tv_star_b);
        mIvStarC = view.findViewById(R.id.tv_star_c);
        mIvStarD = view.findViewById(R.id.tv_star_d);
        mIvStarE = view.findViewById(R.id.tv_star_e);
        initDataStar();
        TextView tvHeightScores = view.findViewById(R.id.btn_height_scores);
        tvHeightScores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initData(mJuniorHighestScore);
            }
        });
        mTvDataDate = view.findViewById(R.id.tv_data_date);
        mTvDataScore = view.findViewById(R.id.tv_data_score);
        mTvDataSpeed = view.findViewById(R.id.tv_data_speed);
        mIvDataVideo = view.findViewById(R.id.iv_data_video);
        fullInfo();
        videoPlayer = view.findViewById(R.id.video_player);
        videoPlayer.setClipToOutline(true);
        videoPlayer.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), 50);
            }
        });
    }

    private void initDataStar() {
//        star_grey_icon
//        star_half_icon
//        star_full_icon
        //1星：0-50，2星：50-100，3星：100-150，4星：150-200，5星>200；
        if (mJuniorHighestScore == null || mJuniorHighestScore.getBlueScore() < 0) {
            return;
        }
        int blueScore = mJuniorHighestScore.getBlueScore();
        if (blueScore > 0 && blueScore < 25) {
            //0.5 star
            mIvStarA.setImageDrawable(Utils.getDrawable(getActivity(), R.drawable.star_half_icon));
        }
        if (blueScore >= 25 && blueScore < 50) {
            //1 star
            mIvStarA.setImageDrawable(Utils.getDrawable(getActivity(), R.drawable.star_full_icon));
        }
        if (blueScore >= 50 && blueScore < 75) {
            //1.5 star
            mIvStarA.setImageDrawable(Utils.getDrawable(getActivity(), R.drawable.star_full_icon));
            mIvStarB.setImageDrawable(Utils.getDrawable(getActivity(), R.drawable.star_half_icon));
        }
        if (blueScore >= 75 && blueScore < 100) {
            //2 star
            mIvStarA.setImageDrawable(Utils.getDrawable(getActivity(), R.drawable.star_full_icon));
            mIvStarB.setImageDrawable(Utils.getDrawable(getActivity(), R.drawable.star_full_icon));
        }

        if (blueScore >= 100 && blueScore < 125) {
            //2.5 star
            mIvStarA.setImageDrawable(Utils.getDrawable(getActivity(), R.drawable.star_full_icon));
            mIvStarB.setImageDrawable(Utils.getDrawable(getActivity(), R.drawable.star_full_icon));
            mIvStarC.setImageDrawable(Utils.getDrawable(getActivity(), R.drawable.star_half_icon));
        }

        if (blueScore >= 125 && blueScore < 150) {
            //3 star
            mIvStarA.setImageDrawable(Utils.getDrawable(getActivity(), R.drawable.star_full_icon));
            mIvStarB.setImageDrawable(Utils.getDrawable(getActivity(), R.drawable.star_full_icon));
            mIvStarC.setImageDrawable(Utils.getDrawable(getActivity(), R.drawable.star_full_icon));
        }

        if (blueScore >= 150 && blueScore < 175) {
            //3.5 star
            mIvStarA.setImageDrawable(Utils.getDrawable(getActivity(), R.drawable.star_full_icon));
            mIvStarB.setImageDrawable(Utils.getDrawable(getActivity(), R.drawable.star_full_icon));
            mIvStarC.setImageDrawable(Utils.getDrawable(getActivity(), R.drawable.star_full_icon));
            mIvStarD.setImageDrawable(Utils.getDrawable(getActivity(), R.drawable.star_half_icon));
        }
        if (blueScore >= 175 && blueScore < 200) {
            //4 star
            mIvStarA.setImageDrawable(Utils.getDrawable(getActivity(), R.drawable.star_full_icon));
            mIvStarB.setImageDrawable(Utils.getDrawable(getActivity(), R.drawable.star_full_icon));
            mIvStarC.setImageDrawable(Utils.getDrawable(getActivity(), R.drawable.star_full_icon));
            mIvStarD.setImageDrawable(Utils.getDrawable(getActivity(), R.drawable.star_full_icon));
        }
        if (blueScore >= 200 && blueScore < 225) {
            //4.5 star
            mIvStarA.setImageDrawable(Utils.getDrawable(getActivity(), R.drawable.star_full_icon));
            mIvStarB.setImageDrawable(Utils.getDrawable(getActivity(), R.drawable.star_full_icon));
            mIvStarC.setImageDrawable(Utils.getDrawable(getActivity(), R.drawable.star_full_icon));
            mIvStarD.setImageDrawable(Utils.getDrawable(getActivity(), R.drawable.star_full_icon));
            mIvStarE.setImageDrawable(Utils.getDrawable(getActivity(), R.drawable.star_half_icon));
        }
        if (blueScore >= 225) {
            //5 star
            mIvStarA.setImageDrawable(Utils.getDrawable(getActivity(), R.drawable.star_full_icon));
            mIvStarB.setImageDrawable(Utils.getDrawable(getActivity(), R.drawable.star_full_icon));
            mIvStarC.setImageDrawable(Utils.getDrawable(getActivity(), R.drawable.star_full_icon));
            mIvStarD.setImageDrawable(Utils.getDrawable(getActivity(), R.drawable.star_full_icon));
            mIvStarE.setImageDrawable(Utils.getDrawable(getActivity(), R.drawable.star_full_icon));
        }
    }

    private void initData(GameAchievement juniorHighestScore) {

        if (juniorHighestScore == null) {
            ToastUtils.showToast("no data");
            return;
        }
        String day = juniorHighestScore.getDay();
        if (!TextUtils.isEmpty(day)) {
            mTvDataDate.setText(day);
        }

        int blueScore = juniorHighestScore.getBlueScore();
        if (blueScore >= 0) {
            mTvDataScore.setText(String.valueOf(blueScore));
        }
        int speed = juniorHighestScore.getSpeed();
        if (speed >= 0) {
            mTvDataSpeed.setText(String.valueOf(speed));
        }

        String videoPath = juniorHighestScore.getVideoPath();
        if (!TextUtils.isEmpty(videoPath)) {
            mIvDataVideo.setVisibility(View.VISIBLE);
            mIvDataVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("zyw", "onClick videoPath = " + videoPath);
                    play(videoPath);
                }
            });
        }

    }

    private void play(String videoPath) {
        videoPlayer.setVisibility(View.VISIBLE);
        videoPlayer.setUp(videoPath, true, "");
        videoPlayer.getBackButton().setVisibility(View.VISIBLE);
        videoPlayer.getFullscreenButton().setVisibility(View.GONE);
        videoPlayer.getBackButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("zyw", "getBackButton onClick");
                videoPlayer.onVideoPause();
                videoPlayer.release();
                videoPlayer.setVisibility(View.GONE);
            }
        });
        //是否可以滑动调整
        videoPlayer.setIsTouchWiget(true);
        //不需要屏幕旋转
        videoPlayer.setNeedOrientationUtils(false);

        videoPlayer.setAutoFullWithSize(true);

        videoPlayer.startPlayLogic();
    }

    private CropEngine getCropFileEngine() {
        return null;
    }

    private UCrop.Options buildOptions() {
        UCrop.Options options = new UCrop.Options();
        options.setCircleDimmedLayer(true);
        options.setHideBottomControls(true);
        options.setFreeStyleCropEnabled(false);
        options.withAspectRatio(1, 1);
        options.setMaxScaleMultiplier(100);
        options.setShowCropFrame(false);
        options.setShowCropGrid(false);
        options.setCropOutputPathDir(getSandboxPath());
        return options;
    }

    private String getSandboxPath() {
        Context context = getContext();
        if (context == null) {
            return "";
        }
        File externalFilesDir = context.getExternalFilesDir("");
        File customFile = new File(externalFilesDir.getAbsolutePath(), "Sandbox");
        if (!customFile.exists()) {
            customFile.mkdirs();
        }
        return customFile.getAbsolutePath() + File.separator;
    }

    private void showDialog(TextView textView) {
        Activity activity = getActivity();
        if (textView == null || activity == null) {
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
                    if (mTvUseNameTop != null) {
                        mTvUseNameTop.setText(content);
                    }
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
            mTvUseNameTop.setText(mUser.name);
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
        videoPlayer.setVisibility(View.GONE);
        videoPlayer.onVideoPause();
        videoPlayer.release();
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