package leonardolana.poppicture.profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.OnClick;
import leonardolana.poppicture.R;
import leonardolana.poppicture.common.BaseDialogFragment;
import leonardolana.poppicture.common.BasePresenter;
import leonardolana.poppicture.helpers.api.PersistentHelper;
import leonardolana.poppicture.helpers.api.TrackingHelper;
import leonardolana.poppicture.helpers.impl.PersistentHelperImpl;
import leonardolana.poppicture.helpers.impl.TrackingHelperImpl;
import leonardolana.poppicture.login.LoginActivity;

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
public class ProfileOnboardingDialogFragment extends BaseDialogFragment implements ProfileOnboardingDialogFragmentView {

    private ProfileOnboardingDialogFragmentPresenter mPresenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasTitle(false);
        setCancelable(false);

        Context applicationContext = getContext().getApplicationContext();
        PersistentHelper persistentHelper = PersistentHelperImpl.getInstance(applicationContext);
        TrackingHelper trackingHelper = TrackingHelperImpl.getInstance(applicationContext);

        mPresenter = new ProfileOnboardingDialogFragmentPresenter(this, persistentHelper, trackingHelper);
    }

    @Override
    protected BasePresenter getPresenter() {
        return mPresenter;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_onboarding, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.button_positive)
    public void onAgreeClick() {
        mPresenter.onSignInClick();
    }

    @OnClick(R.id.button_negative)
    public void onDisagreeClick() {
        mPresenter.onDismissClick();
    }

    // View methods

    @Override
    public void launchAuthentication() {
        Intent intent = new Intent(getContext(), LoginActivity.class);
        startActivity(intent);
        dismiss();
    }
}
