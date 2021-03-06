package leonardolana.poppicture.viewer;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import leonardolana.poppicture.R;
import leonardolana.poppicture.common.AlertDialog;
import leonardolana.poppicture.common.BaseDialogFragment;
import leonardolana.poppicture.common.BasePresenter;
import leonardolana.poppicture.common.ConfirmationDialog;
import leonardolana.poppicture.data.Picture;
import leonardolana.poppicture.data.User;
import leonardolana.poppicture.helpers.api.CacheHelper;
import leonardolana.poppicture.helpers.api.CloudStorage;
import leonardolana.poppicture.helpers.api.PersistentHelper;
import leonardolana.poppicture.helpers.api.RunnableExecutor;
import leonardolana.poppicture.helpers.api.ServerHelper;
import leonardolana.poppicture.helpers.api.TrackingHelper;
import leonardolana.poppicture.helpers.api.UserHelper;
import leonardolana.poppicture.helpers.api.UsersDataHelper;
import leonardolana.poppicture.helpers.impl.CacheHelperImpl;
import leonardolana.poppicture.helpers.impl.CloudStorageImpl;
import leonardolana.poppicture.helpers.impl.ImageHelper;
import leonardolana.poppicture.helpers.impl.MapHelper;
import leonardolana.poppicture.helpers.impl.PersistentHelperImpl;
import leonardolana.poppicture.helpers.impl.RunnableExecutorImpl;
import leonardolana.poppicture.helpers.impl.ServerHelperImpl;
import leonardolana.poppicture.helpers.impl.TrackingHelperImpl;
import leonardolana.poppicture.helpers.impl.UserHelperImpl;
import leonardolana.poppicture.helpers.impl.UsersDataHelperImpl;

/**
 * Created by Leonardo Lana
 * Github: https://github.com/leonardodlana
 * <p>
 * Copyright 2018 Leonardo Lana
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class ViewerFragment extends BaseDialogFragment implements ViewerFragmentView {

    //TODO improve image view visualization

    public static ViewerFragment newInstance(Picture picture) {
        ViewerFragment viewerFragment = new ViewerFragment();
        viewerFragment.setPicture(picture);
        return viewerFragment;
    }

    private UserHelper mUserHelper;
    private CacheHelper mCacheHelper;
    private UsersDataHelper mUsersDataHelper;

    private ViewerFragmentPresenter mPresenter;
    private Picture mPicture;
    private DecimalFormat mDecimalFormat;

    @BindView(R.id.image_view)
    ImageView mImageView;

    @BindView(R.id.loading)
    ProgressBar mProgressBarLoading;

    @BindView(R.id.button_delete)
    AppCompatImageView mButtonDelete;

    @BindView(R.id.button_report)
    AppCompatImageView mButtonReport;

    @BindView(R.id.button_like)
    AppCompatImageView mButtonLike;

    @BindView(R.id.text_user_name)
    TextView mTextUserName;

    @BindView(R.id.text_title)
    TextView mTextTitle;

    @BindView(R.id.text_description)
    TextView mTextDescription;

    @BindView(R.id.text_distance)
    TextView mTextDistance;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreen(true);
        setHasTitle(false);

        Context applicationContext = getContext().getApplicationContext();

        PersistentHelper persistentHelper = PersistentHelperImpl.getInstance(applicationContext);
        mUserHelper = UserHelperImpl.getInstance(persistentHelper);
        mCacheHelper = CacheHelperImpl.getInstance(applicationContext, RunnableExecutorImpl.getInstance());
        ServerHelper serverHelper = ServerHelperImpl.getInstance(applicationContext);
        RunnableExecutor runnableExecutor = RunnableExecutorImpl.getInstance();
        mUsersDataHelper = UsersDataHelperImpl.getInstance(runnableExecutor, serverHelper, mUserHelper);
        CloudStorage cloudStorage = new CloudStorageImpl();
        TrackingHelper trackingHelper = TrackingHelperImpl.getInstance(applicationContext);

        mPresenter = new ViewerFragmentPresenter(this, mPicture, runnableExecutor, serverHelper,
                mUserHelper, cloudStorage, trackingHelper);
        mDecimalFormat = new DecimalFormat("#.##");
    }

    @Override
    protected BasePresenter getPresenter() {
        return mPresenter;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_viewer, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mCacheHelper.loadPicture(mPicture, false, new CacheHelper.OnLoadPicture() {
            @Override
            public void onLoad(Bitmap bitmap) {
                mImageView.setImageBitmap(bitmap);
                mProgressBarLoading.setVisibility(View.GONE);
            }

            @Override
            public void onError(@CacheHelper.LoadError int error) {
                // todo handle each error
                AlertDialog dialog = AlertDialog.newInstance(getString(R.string.error_loading),
                        getString(R.string.error_loading_description));
                dialog.setCancelable(false);
                dialog.setOnDismissListener(new AlertDialog.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        dismiss();
                    }
                });
                dialog.show(getFragmentManager(), "dialog");
            }
        });

        if (mUserHelper.isUserLoggedIn()) {
            mButtonLike.setVisibility(View.VISIBLE);
            if (TextUtils.equals(mPicture.getUserId(), mUserHelper.getPublicId())) {
                mButtonDelete.setVisibility(View.VISIBLE);
            } else {
                mButtonReport.setVisibility(View.VISIBLE);
            }
        }

        mTextTitle.setText(mPicture.getTitle());
        mTextDescription.setText(mPicture.getDescription());

        refreshLike();

        mTextDistance.setText(String.format(getString(R.string.distance_with_km), mDecimalFormat.format(mPicture.getDistanceInKM())));
        mUsersDataHelper.getUser(mPicture.getUserId(), new UsersDataHelper.GetUserResponse() {
            @Override
            public void onSuccess(User user) {
                if (isDetached())
                    return;

                mTextUserName.setText(user.getName());
            }

            @Override
            public void onError() {

            }
        });
    }

    @OnClick(R.id.button_map)
    public void onMapClick() {
        mPresenter.onMapClick();
    }

    @OnClick(R.id.button_close)
    public void onButtonCloseClick() {
        mPresenter.onCloseClick();
    }

    @OnClick(R.id.button_delete)
    public void onButtonDeleteClick() {
        // Confirm dialog then call presenter
        ConfirmationDialog dialog = ConfirmationDialog.newInstance(getString(R.string.confirmation_are_you_sure),
                getString(R.string.confirmation_delete),
                getString(R.string.delete), getString(R.string.cancel));

        dialog.setOnConfirmationDialogListener(new ConfirmationDialog.OnConfirmationDialogListener() {
            @Override
            public void onClickPositive() {
                mPresenter.onDeleteClick();
            }

            @Override
            public void onClickNegative() {
                // ignore
            }
        });

        dialog.show(getFragmentManager(), "dialog");
    }

    @OnClick(R.id.button_report)
    public void onButtonReportClick() {
        // Confirm dialog then call presenter
        ConfirmationDialog dialog = ConfirmationDialog.newInstance(getString(R.string.confirmation_are_you_sure),
                getString(R.string.confirmation_report),
                getString(R.string.report), getString(R.string.cancel));

        dialog.setOnConfirmationDialogListener(new ConfirmationDialog.OnConfirmationDialogListener() {
            @Override
            public void onClickPositive() {
                mPresenter.onClickReport();
                Toast.makeText(getContext(), getString(R.string.thank_for_feedback), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onClickNegative() {
                // ignore
            }
        });

        dialog.show(getFragmentManager(), "dialog");
    }

    @OnClick(R.id.button_like)
    public void onButtonLikeClick() {
        mPresenter.onLikeClick();
    }

    private void setPicture(Picture picture) {
        mPicture = picture;
    }

    /*
        View methods
     */

    @Override
    public void refreshLike() {
        if (mPicture.isLiked())
            mButtonLike.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.picture_liked)));
        else
            mButtonLike.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.picture_not_liked)));
    }

    @Override
    public void showDeleteSuccess() {
        Toast.makeText(getContext(), getString(R.string.photo_deleted), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showDeleteError() {
        AlertDialog dialog = AlertDialog.newInstance(getString(R.string.error_deleting), getString(R.string.error_deleting_description));
        dialog.setCancelable(false);
        dialog.setOnDismissListener(new AlertDialog.OnDismissListener() {
            @Override
            public void onDismiss() {
//                dismiss();
            }
        });
        dialog.show(getFragmentManager(), "dialog");
    }

    @Override
    public void openMap(double latitude, double longitude) {
        MapHelper.openMap(getContext(), latitude, longitude);
    }
}
