package leonardolana.poppicture.about;

import leonardolana.poppicture.common.BasePresenter;
import leonardolana.poppicture.data.TrackingEvent;
import leonardolana.poppicture.helpers.api.TrackingHelper;

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
public class AboutActivityPresenter extends BasePresenter {

    private AboutActivityView mView;
    private TrackingHelper mTrackingHelper;

    public AboutActivityPresenter(AboutActivityView view, TrackingHelper trackingHelper) {
        mView = view;
        mTrackingHelper = trackingHelper;
    }

    @Override
    public void onDestroy() {
        mView = null;
        mTrackingHelper = null;
    }

    /*
        View methods
     */

    public void onClickReview() {
        mTrackingHelper.log(TrackingEvent.ABOUT_CLICK_REVIEW, null);
        mView.openMarket();
    }

    public void onClickPrivacyPolicy() {
        mTrackingHelper.log(TrackingEvent.ABOUT_CLICK_PRIVACY_POLICY, null);
        mView.openPrivacyPolicy();
    }

    public void onClickTermsAndConditions() {
        mTrackingHelper.log(TrackingEvent.ABOUT_CLICK_TERMS_AND_CONDITIONS, null);
        mView.openTermsAndConditions();
    }

    public void onClickGitHub() {
        mTrackingHelper.log(TrackingEvent.ABOUT_CLICK_GITHUB, null);
        mView.openGitHub();
    }
}
